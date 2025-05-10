package com.john.project.test.service.DistributedExecutionMainService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.john.project.enums.DistributedExecutionMainStatusEnum;
import com.john.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionMainServiceGetLastDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        var result = this.distributedExecutionMainService
                .getLastDistributedExecution(storageSpaceCleanDistributedExecution);
        assertTrue(StringUtils.isNotBlank(result.getId()));
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
        var distributedExecutionMainModel = this.distributedExecutionMainService
                .create(storageSpaceCleanDistributedExecution);
        this.distributedExecutionDetailService
                .createByResult(distributedExecutionMainModel.getId(), 1L, 1L);
        this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
    }

}
