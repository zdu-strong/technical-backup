package com.john.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;

import java.util.concurrent.TimeUnit;

public class DistributedExecutionMainServiceHasCanDoneTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionMainService.hasCanDone(this.distributedExecutionMainModel.getId());
        assertTrue(result);
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
        for (var i = distributedExecutionMainModel.getTotalPages(); i > 0; i--) {
            this.distributedExecutionDetailService
                    .createByResult(this.distributedExecutionMainModel.getId(), i, 1L);
        }
    }

}
