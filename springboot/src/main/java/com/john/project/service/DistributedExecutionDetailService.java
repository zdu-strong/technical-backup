package com.john.project.service;

import java.util.Date;
import java.util.Objects;

import com.john.project.entity.DistributedExecutionDetailEntity;
import com.john.project.entity.DistributedExecutionMainEntity;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.john.project.common.baseService.BaseService;
import com.john.project.model.DistributedExecutionDetailModel;
import com.john.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionDetailService extends BaseService {

    public DistributedExecutionDetailModel createByResult(String distributedExecutionMainId, long partitionNum, long pageNum) {
        return this.create(distributedExecutionMainId, partitionNum, pageNum, false);
    }

    public DistributedExecutionDetailModel createByErrorMessage(String distributedExecutionMainId, long partitionNum, long pageNum) {
        return this.create(distributedExecutionMainId, partitionNum, pageNum, true);
    }

    @Transactional(readOnly = true)
    public Long getPageNumByPartitionNum(String distributedExecutionMainId,
                                         long partitionNum) {
        var status = DistributedExecutionMainStatusEnum.IN_PROGRESS.getValue();
        var DistributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(distributedExecutionMainId))
                .where(s -> s.getStatus().equals(status))
                .where(s -> s.getTotalPage() >= partitionNum)
                .findOne()
                .orElse(null);
        if (Objects.isNull(DistributedExecutionMainEntity)) {
            return null;
        }
        var pageNum = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                .where(s -> s.getPartitionNum() == partitionNum)
                .sortedBy(s -> s.getPageNum())
                .findFirst()
                .map(s -> s.getPageNum() - DistributedExecutionMainEntity.getTotalPartition())
                .orElse(null);
        if (pageNum == null) {
            var residue = DistributedExecutionMainEntity.getTotalPage()
                    % DistributedExecutionMainEntity.getTotalPartition();
            if (partitionNum <= residue) {
                pageNum = DistributedExecutionMainEntity.getTotalPage() - residue + partitionNum;
            } else {
                pageNum = DistributedExecutionMainEntity.getTotalPage() - residue
                        - DistributedExecutionMainEntity.getTotalPartition() + partitionNum;
            }
        }

        if (pageNum < 1) {
            return null;
        }
        return pageNum;
    }

    private DistributedExecutionDetailModel create(String distributedExecutionMainId, long partitionNum, long pageNum,
                                                   boolean hasError) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(distributedExecutionMainId))
                .getOnlyValue();

        var distributedExecutionDetailEntity = new DistributedExecutionDetailEntity();
        distributedExecutionDetailEntity.setId(newId());
        distributedExecutionDetailEntity.setCreateDate(new Date());
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        distributedExecutionDetailEntity.setPartitionNum(partitionNum);
        distributedExecutionDetailEntity.setPageNum(pageNum);
        distributedExecutionDetailEntity.setHasError(hasError);
        distributedExecutionDetailEntity.setDistributedExecutionMain(distributedExecutionMainEntity);

        this.persist(distributedExecutionDetailEntity);

        return this.distributedExecutionDetailFormatter.format(distributedExecutionDetailEntity);
    }

}
