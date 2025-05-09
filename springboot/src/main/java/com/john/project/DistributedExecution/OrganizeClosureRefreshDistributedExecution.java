package com.john.project.DistributedExecution;

import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.model.PaginationModel;
import org.springframework.stereotype.Component;

@Component
public class OrganizeClosureRefreshDistributedExecution extends BaseDistributedExecution {

    @Override
    public PaginationModel<?> searchByPagination() {
        return this.organizeService.searchOrganizeByPagination(1L, 1L);
    }

    @Override
    public void executeTask(long pageNum) {
        var paginationModel = this.organizeService.searchOrganizeByPagination(pageNum,
                1L);
        for (var organizeModel : paginationModel.getItems()) {
            while (true) {
                if (this.roleOrganizeRelationService.hasNeededToRefresh(organizeModel.getId())) {
                    var hasNext = this.roleOrganizeRelationService.refresh(organizeModel.getId());
                    if (hasNext) {
                        continue;
                    }
                }
                break;
            }
            while (true) {
                if (this.organizeRelationService.hasNeededToRefresh(organizeModel.getId())) {
                    var hasNext = this.organizeRelationService.refresh(organizeModel.getId());
                    if (hasNext) {
                        continue;
                    }
                }
                break;
            }
        }
    }

}
