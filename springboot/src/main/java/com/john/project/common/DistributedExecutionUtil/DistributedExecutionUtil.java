package com.john.project.common.DistributedExecutionUtil;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.properties.DevelopmentMockModeProperties;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
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

    @Autowired
    private Executor applicationTaskExecutor;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @SneakyThrows
    public void refreshData(BaseDistributedExecution baseDistributedExecution) {
        var distributedExecutionMainModel = this.getDistributedExecution(baseDistributedExecution);
        if (distributedExecutionMainModel == null) {
            return;
        }
        while (true) {
            var partitionNumList = this.getPartitionNumListForSingleMachine(distributedExecutionMainModel, baseDistributedExecution);
            if (partitionNumList.isEmpty()) {
                return;
            }
            runByPartitionNumList(distributedExecutionMainModel, baseDistributedExecution, partitionNumList);
        }
    }

    public void initializeInSystemInitScheduled() {
        if (this.developmentMockModeProperties.getIsUnitTestEnvironment()) {
            return;
        }
        for (var baseDistributedExecution : SpringUtil.getBeansOfType(BaseDistributedExecution.class).values()) {
            var isFirstAtomicBoolean = new AtomicBoolean(true);
            Flowable.just(StringUtils.EMPTY)
                    .observeOn(Schedulers.from(applicationTaskExecutor))
                    .concatMap(s -> {
                        if (isFirstAtomicBoolean.get()) {
                            isFirstAtomicBoolean.set(false);

                            var distributedExecutionMainModel = this.distributedExecutionMainService
                                    .getLastDistributedExecution(baseDistributedExecution);

                            if (isInCooldownPeriod(distributedExecutionMainModel, baseDistributedExecution)) {
                                var delayMilliseconds = Math.abs(Math.subtractExact(DateUtils.addMilliseconds(distributedExecutionMainModel.getUpdateDate(), (int) baseDistributedExecution.getTheIntervalBetweenTwoExecutions().toMillis()).getTime(), new Date().getTime()));
                                return Flowable.timer(delayMilliseconds, TimeUnit.MILLISECONDS);
                            }

                            return Flowable.timer(0, TimeUnit.MILLISECONDS);
                        } else {
                            var delayMilliseconds = Math.min(Duration.ofDays(1).toMillis(), baseDistributedExecution.getTheIntervalBetweenTwoExecutions().toMillis());
                            return Flowable.timer(delayMilliseconds, TimeUnit.MILLISECONDS);
                        }
                    })
                    .observeOn(Schedulers.from(applicationTaskExecutor))
                    .doOnNext(s -> {
                        this.refreshData(baseDistributedExecution);
                    })
                    .repeat()
                    .retry()
                    .subscribe();
        }
    }

    private void runByPartitionNumList(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution, List<Long> partitionNumList) {
        if (partitionNumList.size() == 1) {
            var partitionNum = JinqStream.from(partitionNumList).getOnlyValue();
            runByPartitionNum(distributedExecutionMainModel, baseDistributedExecution, partitionNum);
        } else {
            Flowable.fromIterable(partitionNumList)
                    .parallel(partitionNumList.size())
                    .runOn(Schedulers.from(applicationTaskExecutor))
                    .doOnNext((partitionNum) -> {
                        runByPartitionNum(distributedExecutionMainModel, baseDistributedExecution, partitionNum);
                    })
                    .sequential()
                    .blockingSubscribe();
        }
    }

    private void runByPartitionNum(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution, long partitionNum) {
        this.longTermTaskUtil.runSkipWhenExists(() -> {
            var pageNum = this.distributedExecutionDetailService
                    .getPageNumByPartitionNum(distributedExecutionMainModel.getId(), partitionNum);
            if (pageNum == null) {
                return;
            }
            while (pageNum >= 1) {
                try {
                    baseDistributedExecution.executeTask(pageNum);
                    this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(),
                            partitionNum, pageNum);
                } catch (Throwable e) {
                    this.distributedExecutionDetailService
                            .createByErrorMessage(distributedExecutionMainModel.getId(), partitionNum, pageNum);
                }
                pageNum -= baseDistributedExecution.getMaxNumberOfParallel();
            }
        }, getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, baseDistributedExecution));
    }

    @SneakyThrows
    private DistributedExecutionMainModel getDistributedExecution(BaseDistributedExecution baseDistributedExecution) {
        var deadline = DateUtils.addSeconds(new Date(), 5);
        while (new Date().before(deadline)) {
            {
                var distributedExecutionMainModel = this.distributedExecutionMainService
                        .getLastDistributedExecution(baseDistributedExecution);

                if (isInCooldownPeriod(distributedExecutionMainModel, baseDistributedExecution)) {
                    return null;
                }

                if (isInProgress(distributedExecutionMainModel, baseDistributedExecution)) {
                    return distributedExecutionMainModel;
                }

                if (isAbort(distributedExecutionMainModel, baseDistributedExecution)) {
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
                                .getLastDistributedExecution(baseDistributedExecution);

                        if (isInCooldownPeriod(distributedExecutionMainModel, baseDistributedExecution)) {
                            return;
                        }

                        if (isInProgress(distributedExecutionMainModel, baseDistributedExecution)) {
                            list.add(distributedExecutionMainModel);
                            return;
                        }
                    }

                    {
                        var distributedExecutionMainModel = this.distributedExecutionMainService
                                .create(baseDistributedExecution);
                        if (isInProgress(distributedExecutionMainModel, baseDistributedExecution)) {
                            list.add(distributedExecutionMainModel);
                        }
                    }
                }, getLongTermTaskUniqueKeyModelOfCreateDistributedMain(baseDistributedExecution));

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

    private List<Long> getPartitionNumListForSingleMachine(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution) {
        var partitionNumListForSingleMachine = new ArrayList<Long>();
        var maxNumberOfParallelForSingleMachine = Math.min(Math.min(baseDistributedExecution.getMaxNumberOfParallel(), baseDistributedExecution.getMaxNumberOfParallelForSingleMachine()), distributedExecutionMainModel.getTotalPages());
        for (var i = maxNumberOfParallelForSingleMachine; i > 0; i--) {
            var partitionNum = getPartitionNum(distributedExecutionMainModel, baseDistributedExecution, partitionNumListForSingleMachine);
            if (partitionNum == null) {
                break;
            }
            partitionNumListForSingleMachine.add(partitionNum);
        }
        return partitionNumListForSingleMachine;
    }

    private Long getPartitionNum(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution, List<Long> excludePartitionNumList) {
        var partitionNumList = Flowable.range(1, distributedExecutionMainModel.getTotalPartition().intValue())
                .filter(s -> s <= distributedExecutionMainModel.getTotalPages())
                .filter(s -> !excludePartitionNumList.contains(s))
                .toList()
                .blockingGet();

        while (!partitionNumList.isEmpty()) {
            var partitionNum = partitionNumList.get(RandomUtil.randomInt(0, partitionNumList.size()));
            partitionNumList.removeIf(s -> ObjectUtil.equals(s, partitionNum));

            if (this.longTermTaskService.findOneNotRunning(List.of(getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum, baseDistributedExecution))) == null) {
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

    private boolean isInProgress(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution) {
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> ObjectUtil.equals(s.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()))
                .filter(s -> ObjectUtil.equals(s.getTotalPartition(), baseDistributedExecution.getMaxNumberOfParallel()))
                .isPresent();
    }

    private boolean isAbort(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution) {
        if (Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> ObjectUtil.equals(s.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()))
                .filter(s -> !ObjectUtil.equals(s.getTotalPartition(), baseDistributedExecution.getMaxNumberOfParallel()))
                .isPresent()
        ) {
            this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
            return true;
        }
        return false;
    }

    private boolean isInCooldownPeriod(DistributedExecutionMainModel distributedExecutionMainModel, BaseDistributedExecution baseDistributedExecution) {
        return Optional.ofNullable(distributedExecutionMainModel)
                .filter(s -> Stream
                        .of(DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE,
                                DistributedExecutionMainStatusEnum.ERROR_END)
                        .anyMatch(m -> ObjectUtil.equals(s.getStatus(), m.getValue()))
                )
                .filter(s -> !new Date().after(DateUtils
                        .addMilliseconds(s.getUpdateDate(),
                                (int) baseDistributedExecution.getTheIntervalBetweenTwoExecutions().toMillis())))
                .isPresent();
    }

    @SneakyThrows
    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelByPartitionNum(long partitionNum, BaseDistributedExecution baseDistributedExecution) {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.getValue())
                .setUniqueKey(this.objectMapper
                        .writeValueAsString(new Pair<>(baseDistributedExecution.getClass().getSimpleName(), partitionNum)));
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelOfCreateDistributedMain(BaseDistributedExecution baseDistributedExecution) {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.CREATE_DISTRIBUTED_EXECUTION_MAIN.getValue())
                .setUniqueKey(baseDistributedExecution.getClass().getSimpleName());
    }

}
