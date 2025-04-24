package com.john.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionMainServiceHasCanRefreshDistributedExecutionTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionMainService.hasCanDone(this.distributedExecutionMainModel.getId());
        assertTrue(result);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
        this.distributedExecutionDetailService
                .createByResult(this.distributedExecutionMainModel.getId(), 1L, 1L);
    }

}
