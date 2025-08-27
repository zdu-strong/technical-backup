package com.john.project.test.common.DistributedExecutionUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DistributedExecutionUtilRefreshDataTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        this.distributedExecutionUtil.refreshData(storageSpaceCleanDistributedExecution);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.createTempFolder();
        Flowable.interval(0, 1, TimeUnit.MILLISECONDS)
                .filter(s -> this.storageSpaceCleanDistributedExecution.searchByPagination().getTotalRecords() > 0)
                .take(1)
                .blockingSubscribe();
    }

}
