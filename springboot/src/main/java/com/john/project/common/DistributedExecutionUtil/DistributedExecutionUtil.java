package com.john.project.common.DistributedExecutionUtil;

import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.service.DistributedExecutionMainService;
import com.john.project.service.LongTermTaskService;
import cn.hutool.core.util.RandomUtil;
import io.reactivex.rxjava3.core.Flowable;
import com.john.project.service.DistributedExecutionDetailService;
import lombok.SneakyThrows;

@Component
public class DistributedExecutionUtil {

    @Autowired
    private DistributedExecutionMainService distributedExecutionMainService;

    @Autowired
    private DistributedExecutionDetailService distributedExecutionDetailService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LongTermTaskService longTermTaskService;

    @SneakyThrows
    public void refreshData(DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionMainModel = this.getDistributedExecution(distributedExecutionEnum);
        if (distributedExecutionMainModel == null) {
            return;
        }
        while (true) {
            var partitionNum = this.getPartitionNum(distributedExecutionMainModel, distributedExecutionEnum);
            if (partitionNum == null) {
                return;
            }

            this.longTermTaskUtil.runSkipWhenExists(() -> {
                var pageNum = this.distributedExecutionDetailService
                        .getPageNumByPartitionNum(distributedExecutionMainModel.getId(), partitionNum);
                if (pageNum == null) {
                    return;
                }
                while (pageNum >= 1) {
                    try {
                        distributedExecutionEnum.getCallbackOfExecuteTask().accept(pageNum);
                        this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(),
                                partitionNum, pageNum);
                    } catch (Throwable e) {
                        this.distributedExecutionDetailService
                                .createByErrorMessage(distributedExecutionMainModel.getId(), partitionNum, pageNum);
                    }
                    pageNum -= distributedExecutionEnum.getMaxNumberOfParallel();
                }
            }, getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, distributedExecutionEnum));
        }
    }

    private DistributedExecutionMainModel getDistributedExecution(DistributedExecutionEnum distributedExecutionEnum) {
        {
            var distributedExecutionMainModel = this.distributedExecutionMainService
                    .getLastDistributedExecution(distributedExecutionEnum);
            if (isInProgress(distributedExecutionMainModel, distributedExecutionEnum)) {
                return distributedExecutionMainModel;
            }
        }

        if (isInCooldownPeriod(distributedExecutionEnum)) {
            return null;
        }

        {
            var list = new ArrayList<DistributedExecutionMainModel>();
            this.longTermTaskUtil.runSkipAfterRetryWhenExists(() -> {
                if (isInCooldownPeriod(distributedExecutionEnum)) {
                    return;
                }

                var distributedExecutionMainModel = this.distributedExecutionMainService
                        .create(distributedExecutionEnum);
                if (isInProgress(distributedExecutionMainModel, distributedExecutionEnum)) {
                    list.add(distributedExecutionMainModel);
                }
            }, getLongTermTaskUniqueKeyModelOfCreateDistributedMain());
            return JinqStream.from(list).findOne().orElse(null);
        }
    }

    private Long getPartitionNum(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionEnum distributedExecutionEnum) {
        var partitionNumList = Flowable.range(1, distributedExecutionMainModel.getTotalPartition().intValue())
                .filter(s -> s <= distributedExecutionMainModel.getTotalPage())
                .toList()
                .blockingGet();

        while (!partitionNumList.isEmpty()) {
            var partitionNum = partitionNumList.get(RandomUtil.randomInt(0, partitionNumList.size()));
            partitionNumList.removeIf(s -> Objects.equals(s, partitionNum));

            var pageNum = this.distributedExecutionDetailService.getPageNumByPartitionNum(distributedExecutionMainModel.getId(),
                    partitionNum);
            if (pageNum == null) {
                continue;
            }
            var longTermTaskUniqueKeyModel = this.getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, distributedExecutionEnum);
            if (this.longTermTaskService.findOneNotRunning(List.of(longTermTaskUniqueKeyModel)) == null) {
                continue;
            }
            return (long) partitionNum;
        }

        if (this.distributedExecutionMainService.hasCanDone(distributedExecutionMainModel.getId())) {
            this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
        }
        return null;
    }

    private boolean isInProgress(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionEnum distributedExecutionEnum) {
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> Objects.equals(s.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()))
                .filter(s -> Objects.equals(s.getTotalPartition(), distributedExecutionEnum.getMaxNumberOfParallel()))
                .isPresent();
    }

    private boolean isInCooldownPeriod(DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionMainModel = this.distributedExecutionMainService
                .getLastDistributedExecution(distributedExecutionEnum);
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> Stream
                        .of(DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE,
                                DistributedExecutionMainStatusEnum.ERROR_END)
                        .anyMatch(m -> Objects.equals(s.getStatus(), m.getValue()))
                )
                .filter(s -> !new Date().after(DateUtils
                        .addMilliseconds(s.getUpdateDate(),
                                (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis())))
                .isPresent();
    }

    @SneakyThrows
    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelByPartitionNum(long partitionNum,
                                                                                   DistributedExecutionEnum distributedExecutionEnum) {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.getValue())
                .setUniqueKey(this.objectMapper
                        .writeValueAsString(new Pair<>(distributedExecutionEnum.getValue(), partitionNum)));
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelOfCreateDistributedMain() {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.CREATE_DISTRIBUTED_EXECUTION_MAIN.getValue())
                .setUniqueKey(StringUtils.EMPTY);
    }

}
