package com.john.project.service;

import java.util.Date;
import java.util.Objects;

import com.john.project.entity.DistributedExecutionDetailEntity;
import com.john.project.entity.DistributedExecutionMainEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import com.john.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionMainService extends BaseService {

    public DistributedExecutionMainModel create(DistributedExecutionEnum distributedExecutionEnum) {
        {
            var executionType = distributedExecutionEnum.getValue();
            var status = DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue();
            var distributedExecutionMainList = this.streamAll(DistributedExecutionMainEntity.class)
                    .where(s -> s.getExecutionType().equals(executionType))
                    .where(s -> s.getStatus().equals(status))
                    .sortedDescendingBy(s -> s.getId())
                    .sortedDescendingBy(s -> s.getCreateDate())
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
        distributedExecutionMainEntity.setStatus(getStatus(distributedExecutionMainEntity));
        this.persist(distributedExecutionMainEntity);

        return this.distributedExecutionMainFormatter.format(distributedExecutionMainEntity);
    }


    public void updateWithDone(String id) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionMainEntity.setStatus(getStatus(distributedExecutionMainEntity));
        distributedExecutionMainEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionMainEntity);
    }

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


    @Transactional(readOnly = true)
    public boolean hasCanDone(String id) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!Objects.equals(distributedExecutionMainEntity.getStatus(), DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue())) {
            return false;
        }

        var status = getStatus(distributedExecutionMainEntity);
        return !Objects.equals(status, DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue());
    }

    private String getStatus(DistributedExecutionMainEntity distributedExecutionMainEntity) {
        if (StringUtils.isNotBlank(distributedExecutionMainEntity.getStatus()) && !Objects.equals(DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue(), distributedExecutionMainEntity.getStatus())) {
            return distributedExecutionMainEntity.getStatus();
        }

        if (distributedExecutionMainEntity.getTotalPage() <= 0) {
            return DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE.getValue();
        }
        if (!Objects.equals(distributedExecutionMainEntity.getTotalPartition(), DistributedExecutionEnum.parse(distributedExecutionMainEntity.getExecutionType()).getMaxNumberOfParallel())) {
            return DistributedExecutionMainStatusEnum.ABORTED.getValue();
        }

        var id = distributedExecutionMainEntity.getId();
        var totalPartition = Math.min(distributedExecutionMainEntity.getTotalPartition(), distributedExecutionMainEntity.getTotalPage());
        var totalPageOfDistributedExecutionTaskWithDone = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getPageNum() <= totalPartition)
                .count();
        if (totalPageOfDistributedExecutionTaskWithDone < totalPartition) {
            return DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue();
        }
        var hasError = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getHasError())
                .exists();
        return hasError ? DistributedExecutionMainStatusEnum.ERROR_END.getValue() : DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE.getValue();
    }

}
