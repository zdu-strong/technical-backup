package com.john.project.DistributedExecution;

import cn.hutool.core.text.StrFormatter;
import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.PaginationModel;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static eu.ciechanowiec.sneakyfun.SneakyRunnable.sneaky;

@Component
public class StorageSpaceCleanDistributedExecution extends BaseDistributedExecution {

    @Override
    public PaginationModel<?> searchByPagination() {
        return this.storageSpaceService.searchStorageSpaceByPagination(1L, 1L);
    }

    @Override
    public void executeTask(long pageNum) {
        var paginationModel = this.storageSpaceService.searchStorageSpaceByPagination(pageNum, 1L);
        for (var storageSpaceModel : paginationModel.getItems()) {
            var folderName = storageSpaceModel.getFolderName();
            if (!this.storageSpaceService.isUsed(folderName)) {
                this.longTermTaskUtil.runSkipWhenExists(sneaky(() -> {
                    if (!this.storageSpaceService.isUsed(folderName)) {
                        CompletableFuture.runAsync(() -> {
                            var request = new MockHttpServletRequest();
                            request.setRequestURI(this.storage.getResoureUrlFromResourcePath(folderName));
                            this.storage.delete(request);
                            if (new File(this.storage.getRootPath(), folderName).exists()) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        StrFormatter.format("Folder deletion failed. FolderName:{}", folderName));
                            }
                            this.storageSpaceService.delete(folderName);
                        }, applicationTaskExecutor).get();
                    }
                }), getLongTermTaskUniqueKeyModel(folderName));
            }
        }
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModel(String folderName) {
        var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.REFRESH_STORAGE_SPACE.getValue())
                .setUniqueKey(folderName);
        return longTermTaskUniqueKeyModel;
    }

}
