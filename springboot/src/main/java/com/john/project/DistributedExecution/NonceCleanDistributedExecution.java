package com.john.project.DistributedExecution;

import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.model.PaginationModel;
import org.springframework.stereotype.Component;

@Component
public class NonceCleanDistributedExecution extends BaseDistributedExecution {

    @Override
    public PaginationModel<?> searchByPagination() {
        return this.nonceService.searchNonceByPagination(1L, 1L);
    }

    @Override
    public void executeTask(long pageNum) {
        var paginationModel = this.nonceService.searchNonceByPagination(pageNum,
                1L);
        for (var nonceModel : paginationModel.getItems()) {
            this.nonceService.delete(nonceModel.getId());
        }
    }

}
