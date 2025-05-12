package com.john.project.test.common.DistributedExecution.StorageSpaceCleanDistributedExecution;

import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class StorageSpaceCleanDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(storageSpaceCleanDistributedExecution);
    }

}
