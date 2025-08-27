package com.john.project.test.service.DistributedExecutionDetailService;

import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;

public class DistributedExecutionDetailServiceGetPageNumByPartitionNumDistributedExecutionMainHasDoneTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionDetailService.getPageNumByPartitionNum(distributedExecutionMainModel.getId(), 1);
        assertNull(result);
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
        this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(), 1, 1);
        if (this.distributedExecutionMainService.hasCanDone(distributedExecutionMainModel.getId())) {
            this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
        }
    }

}
