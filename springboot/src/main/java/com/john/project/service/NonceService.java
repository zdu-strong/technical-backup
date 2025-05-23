package com.john.project.service;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.constant.NonceConstant;
import com.john.project.entity.NonceEntity;
import com.john.project.model.NonceModel;
import com.john.project.model.PaginationModel;

@Service
public class NonceService extends BaseService {

    public NonceModel create(String nonce) {
        var nonceEntity = new NonceEntity();
        nonceEntity.setId(newId());
        nonceEntity.setNonce(nonce);
        nonceEntity.setCreateDate(new Date());
        nonceEntity.setUpdateDate(new Date());
        this.persist(nonceEntity);

        return this.nonceFormatter.format(nonceEntity);
    }

    public PaginationModel<NonceModel> searchNonceByPagination(Long pageNum, Long pageSize) {
        var expiredDate = DateUtils.addMilliseconds(new Date(),
                (int) -NonceConstant.NONCE_SURVIVAL_DURATION.plusHours(1).toMillis());
        var stream = this.streamAll(NonceEntity.class)
                .where(s -> s.getCreateDate().before(expiredDate))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, this.nonceFormatter::format);
    }

    public void delete(String id) {
        var nonceEntity = this.streamAll(NonceEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(nonceEntity);
    }

}
