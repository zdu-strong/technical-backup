package com.john.project.enums;

import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import cn.hutool.core.text.StrFormatter;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.storage.Storage;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.PaginationModel;
import com.john.project.service.NonceService;
import com.john.project.service.OrganizeRelationService;
import com.john.project.service.OrganizeService;
import com.john.project.service.RoleOrganizeRelationService;
import com.john.project.service.StorageSpaceService;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static eu.ciechanowiec.sneakyfun.SneakyRunnable.sneaky;

@AllArgsConstructor
@Getter
public enum DistributedExecutionEnum {

    /**
     * Storage space cleans up the stored data in the database
     */
    STORAGE_SPACE_CLEAN(
            "STORAGE_SPACE_CLEAN",
            Duration.ofHours(12),
            1L,
            () -> {
                return SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(1L, 1L);
            },
            (pageNum) -> {
                var storageSpaceService = SpringUtil.getBean(StorageSpaceService.class);
                var storage = SpringUtil.getBean(Storage.class);
                var longTermTaskUtil = SpringUtil.getBean(LongTermTaskUtil.class);
                var paginationModel = storageSpaceService.getStorageSpaceByPagination(pageNum,
                        1L);
                for (var storageSpaceModel : paginationModel.getItems()) {
                    var folderName = storageSpaceModel.getFolderName();
                    var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                            .setType(LongTermTaskTypeEnum.REFRESH_STORAGE_SPACE.getValue())
                            .setUniqueKey(folderName);
                    if (!storageSpaceService.isUsed(folderName)) {
                        longTermTaskUtil.runSkipWhenExists(sneaky(() -> {
                            if (!storageSpaceService.isUsed(folderName)) {
                                var gracefulExecutor = SpringUtil.getBean("applicationTaskExecutor", TaskExecutor.class);
                                CompletableFuture.runAsync(() -> {
                                    var request = new MockHttpServletRequest();
                                    request.setRequestURI(storage.getResoureUrlFromResourcePath(folderName));
                                    storage.delete(request);
                                    if (new File(storage.getRootPath(), folderName).exists()) {
                                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                StrFormatter.format("Folder deletion failed. FolderName:{}", folderName));
                                    }
                                    storageSpaceService.delete(folderName);
                                }, gracefulExecutor).get();
                            }
                        }), longTermTaskUniqueKeyModel);
                    }
                }
            }),

    /**
     * Clean outdated nonce data in the database
     */
    NONCE_CLEAN(
            "NONCE_CLEAN",
            Duration.ofHours(12),
            1L,
            () -> {
                return SpringUtil.getBean(NonceService.class).searchNonceByPagination(1L, 1L);
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(NonceService.class).searchNonceByPagination(pageNum,
                        1L);
                for (var nonceModel : paginationModel.getItems()) {
                    SpringUtil.getBean(NonceService.class).delete(nonceModel.getId());
                }
            }),

    /**
     * The OrganizeEntity refreshes the data of the OrganizeRelationEntity.
     */
    ORGANIZE_CLOSURE_REFRESH(
            "ORGANIZE_CLOSURE_REFRESH",
            Duration.ofMinutes(1),
            1L,
            () -> {
                return SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(1L, 1L);
            },
            (pageNum) -> {
                var organizeService = SpringUtil.getBean(OrganizeService.class);
                var roleOrganizeRelationService = SpringUtil.getBean(RoleOrganizeRelationService.class);
                var organizeRelationService = SpringUtil.getBean(OrganizeRelationService.class);
                var paginationModel = organizeService.getOrganizeByPagination(pageNum,
                        1L);
                for (var organizeModel : paginationModel.getItems()) {
                    while (true) {
                        if (roleOrganizeRelationService.hasNeededToRefresh(organizeModel.getId())) {
                            var hasNext = roleOrganizeRelationService.refresh(organizeModel.getId());
                            if (hasNext) {
                                continue;
                            }
                        }
                        break;
                    }
                    while (true) {
                        if (organizeRelationService.hasNeededToRefresh(organizeModel.getId())) {
                            var hasNext = organizeRelationService.refresh(organizeModel.getId());
                            if (hasNext) {
                                continue;
                            }
                        }
                        break;
                    }
                }
            });

    private final String value;

    private final Duration theIntervalBetweenTwoExecutions;

    private final long maxNumberOfParallel;

    private final Supplier<PaginationModel<?>> callbackOfGetPagination;

    private final Consumer<Long> callbackOfExecuteTask;

    public static DistributedExecutionEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(DistributedExecutionEnum::getValue, value)).get();
    }

}
