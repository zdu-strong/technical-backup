package com.john.project.test.service.DistributedExecutionDetailService;

import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
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
        this.distributedExecutionMainModel = this.distributedExecutionMainService
                .create(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
        this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(), 1, 1);
        if (this.distributedExecutionMainService.hasCanDone(distributedExecutionMainModel.getId())) {
            this.distributedExecutionMainService.updateWithDone(distributedExecutionMainModel.getId());
        }
    }

}
