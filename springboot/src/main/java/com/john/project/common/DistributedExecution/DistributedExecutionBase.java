package com.john.project.common.DistributedExecution;

import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.storage.Storage;
import com.john.project.model.PaginationModel;
import com.john.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import java.time.Duration;

public abstract class DistributedExecutionBase {

    @Autowired
    protected NonceService nonceService;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected RoleOrganizeRelationService roleOrganizeRelationService;

    @Autowired
    protected OrganizeRelationService organizeRelationService;

    @Autowired
    protected StorageSpaceService storageSpaceService;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

    @Autowired
    protected Storage storage;

    @Resource
    protected TaskExecutor applicationTaskExecutor;

    public abstract PaginationModel<?> searchByPagination();

    public abstract void executeTask(long pageNum);

    public long getMaxNumberOfParallel() {
        return 1;
    }

    public Duration getTheIntervalBetweenTwoExecutions() {
        return Duration.ofHours(12);
    }
}
