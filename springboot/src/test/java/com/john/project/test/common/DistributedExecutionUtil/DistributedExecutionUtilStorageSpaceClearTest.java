package com.john.project.test.common.DistributedExecutionUtil;

import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DistributedExecutionUtilStorageSpaceClearTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN);
    }

    @BeforeEach
    public void beforeEach() {
        this.storage.createTempFolder();
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil)
                .refreshData(Mockito.any());
    }

}
