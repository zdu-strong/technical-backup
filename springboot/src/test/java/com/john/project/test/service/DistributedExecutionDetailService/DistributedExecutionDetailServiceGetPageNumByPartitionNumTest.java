package com.john.project.test.service.DistributedExecutionDetailService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;

import java.util.concurrent.TimeUnit;

public class DistributedExecutionDetailServiceGetPageNumByPartitionNumTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionDetailService.getPageNumByPartitionNum(distributedExecutionMainModel.getId(), 1);
        assertEquals(1, result);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        Flowable.interval(0, 1, TimeUnit.MILLISECONDS)
                .filter(s -> this.storageSpaceCleanDistributedExecution.searchByPagination().getTotalRecords() > 0)
                .take(1)
                .blockingSubscribe();
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(storageSpaceCleanDistributedExecution);
    }

}
