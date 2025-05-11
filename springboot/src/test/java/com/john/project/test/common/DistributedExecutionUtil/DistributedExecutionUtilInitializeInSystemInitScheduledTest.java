package com.john.project.test.common.DistributedExecutionUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DistributedExecutionUtilInitializeInSystemInitScheduledTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        this.distributedExecutionUtil.initializeInSystemInitScheduled();
    }

}
