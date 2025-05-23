package com.john.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionMainServiceUpdateWithDoneTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        this.distributedExecutionMainService.updateWithDone(this.distributedExecutionMainModel.getId());
        var result = this.distributedExecutionMainService
                .getLastDistributedExecution(storageSpaceCleanDistributedExecution);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(this.distributedExecutionMainModel.getId(), result.getId());
        assertEquals(storageSpaceCleanDistributedExecution.getClass().getSimpleName(), result.getExecutionType());
        assertEquals(DistributedExecutionMainStatusEnum.SUCCESS_COMPLETE.getValue(), result.getStatus());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalPartition());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(storageSpaceCleanDistributedExecution);
        this.distributedExecutionDetailService
                .createByResult(this.distributedExecutionMainModel.getId(), 1L, 1L);
        var result = this.distributedExecutionMainService.hasCanDone(this.distributedExecutionMainModel.getId());
        assertTrue(result);
    }

}
