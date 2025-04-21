package com.john.project.service;

import java.util.Date;

import com.john.project.entity.DistributedExecutionDetailEntity;
import com.john.project.entity.DistributedExecutionMainEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import com.john.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionMainService extends BaseService {

    @Transactional(readOnly = true)
    public DistributedExecutionMainModel getLastDistributedExecution(
            DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getValue();
        var distributedExecutionMainModel = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate())
                .findFirst()
                .map(this.distributedExecutionMainFormatter::format)
                .orElse(null);
        return distributedExecutionMainModel;
    }

    public DistributedExecutionMainModel create(DistributedExecutionEnum distributedExecutionEnum) {
        {
            var status = DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue();
            var distributedExecutionMainList = this.streamAll(DistributedExecutionMainEntity.class)
                    .where(s -> status.equals(s.getStatus()))
                    .toList();
            for (var distributedExecutionMainEntity : distributedExecutionMainList) {
                if (distributedExecutionMainEntity.getTotalPartition() == distributedExecutionEnum
                        .getMaxNumberOfParallel()) {
                    return this.distributedExecutionMainFormatter.format(distributedExecutionMainEntity);
                } else {
                    distributedExecutionMainEntity.setStatus(DistributedExecutionMainStatusEnum.ABORTED.getValue());
                    distributedExecutionMainEntity.setUpdateDate(new Date());
                    this.merge(distributedExecutionMainEntity);
                }
            }
        }

        var distributedExecutionMainEntity = new DistributedExecutionMainEntity();
        distributedExecutionMainEntity.setId(newId());
        distributedExecutionMainEntity.setCreateDate(new Date());
        distributedExecutionMainEntity.setUpdateDate(new Date());
        distributedExecutionMainEntity.setExecutionType(distributedExecutionEnum.getValue());
        distributedExecutionMainEntity.setTotalPage(distributedExecutionEnum.getCallbackOfGetPagination().get().getTotalPages());
        distributedExecutionMainEntity.setTotalPartition(distributedExecutionEnum.getMaxNumberOfParallel());
        distributedExecutionMainEntity.setStatus(DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue());
        if (distributedExecutionMainEntity.getTotalPage() <= 0) {
            distributedExecutionMainEntity.setStatus(DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE.getValue());
        }
        this.persist(distributedExecutionMainEntity);

        return this.distributedExecutionMainFormatter.format(distributedExecutionMainEntity);
    }

    @Transactional(readOnly = true)
    public boolean hasCanRefreshDistributedExecution(String id) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue()
                .equals(distributedExecutionMainEntity.getStatus())) {
            return false;
        }

        var totalPartition = Math.min(distributedExecutionMainEntity.getTotalPartition(), distributedExecutionMainEntity.getTotalPage());

        var totalPageOfDistributedExecutionTaskWithDone = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getPageNum() <= totalPartition)
                .count();
        if (totalPageOfDistributedExecutionTaskWithDone < totalPartition) {
            return false;
        }
        return true;
    }

    public void handleDoneDistributedExecution(String id) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionMainEntity.setStatus(getStatusWithDistributedExecution(id));
        distributedExecutionMainEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionMainEntity);
    }

    private String getStatusWithDistributedExecution(String id) {
        var hasError = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getHasError())
                .exists();
        return hasError ? DistributedExecutionMainStatusEnum.ERROR_END.getValue() : DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE.getValue();
    }

}
