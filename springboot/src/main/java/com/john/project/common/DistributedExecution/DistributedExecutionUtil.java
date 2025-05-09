package com.john.project.common.DistributedExecution;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
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
    public void refreshData(DistributedExecutionBase distributedExecutionBase) {
        var distributedExecutionMainModel = this.getDistributedExecution(distributedExecutionBase);
        if (distributedExecutionMainModel == null) {
            return;
        }
        while (true) {
            var partitionNum = this.getPartitionNum(distributedExecutionMainModel, distributedExecutionBase);
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
                        distributedExecutionBase.executeTask(pageNum);
                        this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(),
                                partitionNum, pageNum);
                    } catch (Throwable e) {
                        this.distributedExecutionDetailService
                                .createByErrorMessage(distributedExecutionMainModel.getId(), partitionNum, pageNum);
                    }
                    pageNum -= distributedExecutionBase.getMaxNumberOfParallel();
                }
            }, getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, distributedExecutionBase));
        }
    }

    @SneakyThrows
    private DistributedExecutionMainModel getDistributedExecution(DistributedExecutionBase distributedExecutionBase) {
        var deadline = DateUtils.addSeconds(new Date(), 5);
        while (new Date().before(deadline)) {
            {
                var distributedExecutionMainModel = this.distributedExecutionMainService
                        .getLastDistributedExecution(distributedExecutionBase);

                if (isInCooldownPeriod(distributedExecutionMainModel, distributedExecutionBase)) {
                    return null;
                }

                if (isInProgress(distributedExecutionMainModel, distributedExecutionBase)) {
                    return distributedExecutionMainModel;
                }

                if (isAbort(distributedExecutionMainModel, distributedExecutionBase)) {
                    continue;
                }
            }

            {
                var list = new ArrayList<DistributedExecutionMainModel>();
                var isEnd = new AtomicBoolean(false);

                this.longTermTaskUtil.runSkipWhenExists(() -> {
                    isEnd.set(true);

                    {
                        var distributedExecutionMainModel = this.distributedExecutionMainService
                                .getLastDistributedExecution(distributedExecutionBase);

                        if (isInCooldownPeriod(distributedExecutionMainModel, distributedExecutionBase)) {
                            return;
                        }

                        if (isInProgress(distributedExecutionMainModel, distributedExecutionBase)) {
                            list.add(distributedExecutionMainModel);
                            return;
                        }
                    }

                    {
                        var distributedExecutionMainModel = this.distributedExecutionMainService
                                .create(distributedExecutionBase);
                        if (isInProgress(distributedExecutionMainModel, distributedExecutionBase)) {
                            list.add(distributedExecutionMainModel);
                        }
                    }
                }, getLongTermTaskUniqueKeyModelOfCreateDistributedMain());

                if (!list.isEmpty()) {
                    return JinqStream.from(list).getOnlyValue();
                }

                if (isEnd.get()) {
                    return null;
                }
            }

            Thread.sleep(100);
        }

        return null;
    }

    private Long getPartitionNum(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionBase distributedExecutionBase) {
        var partitionNumList = Flowable.range(1, distributedExecutionMainModel.getTotalPartition().intValue())
                .filter(s -> s <= distributedExecutionMainModel.getTotalPage())
                .toList()
                .blockingGet();

        while (!partitionNumList.isEmpty()) {
            var partitionNum = partitionNumList.get(RandomUtil.randomInt(0, partitionNumList.size()));
            partitionNumList.removeIf(s -> ObjectUtil.equals(s, partitionNum));

            if (this.longTermTaskService.findOneNotRunning(List.of(getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, distributedExecutionBase))) == null) {
                continue;
            }

            var pageNum = this.distributedExecutionDetailService.getPageNumByPartitionNum(distributedExecutionMainModel.getId(),
                    partitionNum);
            if (pageNum == null) {
                if (partitionNumList.isEmpty()) {
                    if (this.distributedExecutionMainService.hasCanDone(distributedExecutionMainModel.getId())) {
                        this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
                    }
                }
                continue;
            }

            return (long) partitionNum;
        }

        return null;
    }

    private boolean isInProgress(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionBase distributedExecutionBase) {
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> ObjectUtil.equals(s.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()))
                .filter(s -> ObjectUtil.equals(s.getTotalPartition(), distributedExecutionBase.getMaxNumberOfParallel()))
                .isPresent();
    }

    private boolean isAbort(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionBase distributedExecutionBase) {
        if (Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> ObjectUtil.equals(s.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()))
                .filter(s -> !ObjectUtil.equals(s.getTotalPartition(), distributedExecutionBase.getMaxNumberOfParallel()))
                .isPresent()
        ) {
            this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
            return true;
        }
        return false;
    }

    private boolean isInCooldownPeriod(DistributedExecutionMainModel distributedExecutionMainModel, DistributedExecutionBase distributedExecutionBase) {
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> Stream
                        .of(DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE,
                                DistributedExecutionMainStatusEnum.ERROR_END)
                        .anyMatch(m -> ObjectUtil.equals(s.getStatus(), m.getValue()))
                )
                .filter(s -> !new Date().after(DateUtils
                        .addMilliseconds(s.getUpdateDate(),
                                (int) distributedExecutionBase.getTheIntervalBetweenTwoExecutions().toMillis())))
                .isPresent();
    }

    @SneakyThrows
    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelByPartitionNum(long partitionNum, DistributedExecutionBase distributedExecutionBase) {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.getValue())
                .setUniqueKey(this.objectMapper
                        .writeValueAsString(new Pair<>(distributedExecutionBase.getClass().getSimpleName(), partitionNum)));
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelOfCreateDistributedMain() {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.CREATE_DISTRIBUTED_EXECUTION_MAIN.getValue())
                .setUniqueKey(StringUtils.EMPTY);
    }

}
