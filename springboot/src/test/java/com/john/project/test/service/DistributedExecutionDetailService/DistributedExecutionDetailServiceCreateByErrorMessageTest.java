package com.john.project.test.service.DistributedExecutionDetailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class DistributedExecutionDetailServiceCreateByErrorMessageTest extends BaseTest {

    private DistributedExecutionMainModel distributedExecutionMainModel;

    @Test
    public void test() {
        var result = this.distributedExecutionDetailService.createByErrorMessage(distributedExecutionMainModel.getId(), 1, 1);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(result.getHasError());
        assertEquals(1, result.getPageNum());
        assertEquals(1, result.getPartitionNum());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(this.distributedExecutionMainModel.getId(), result.getDistributedExecutionMain().getId());
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.storageResource(new ClassPathResource("email/email.xml"));
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(storageSpaceCleanDistributedExecution);
    }

}
