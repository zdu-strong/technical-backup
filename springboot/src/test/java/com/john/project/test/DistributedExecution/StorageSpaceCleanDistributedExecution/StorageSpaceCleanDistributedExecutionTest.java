package com.john.project.test.DistributedExecution.StorageSpaceCleanDistributedExecution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.john.project.test.common.BaseTest.BaseTest;

public class StorageSpaceCleanDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(storageSpaceCleanDistributedExecution);
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil)
                .refreshData(Mockito.any());
    }

}
