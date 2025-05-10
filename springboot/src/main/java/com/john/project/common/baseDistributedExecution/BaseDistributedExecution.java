package com.john.project.common.baseDistributedExecution;

import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.storage.Storage;
import com.john.project.model.PaginationModel;
import com.john.project.service.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
import java.util.concurrent.Executor;

public abstract class BaseDistributedExecution {

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
    protected Executor applicationTaskExecutor;

    public abstract PaginationModel<?> searchByPagination();

    public abstract void executeTask(long pageNum);

    public long getMaxNumberOfParallel() {
        return 1;
    }

    public Duration getTheIntervalBetweenTwoExecutions() {
        return Duration.ofHours(12);
    }

    public long getMaxNumberOfParallelForSingleMachine() {
        return 1;
    }

}
