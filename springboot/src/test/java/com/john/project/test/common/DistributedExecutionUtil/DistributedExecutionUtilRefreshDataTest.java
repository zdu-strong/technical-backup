package com.john.project.test.common.DistributedExecutionUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DistributedExecutionUtilRefreshDataTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        this.distributedExecutionUtil.refreshData(storageSpaceCleanDistributedExecution);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.createTempFolder();
    }

}
