package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenWithdrawalTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var result = this.lumenContext.withdrawal(lumenContext.getUsd(), new BigDecimal(100));
        assertTrue(ObjectUtil.equals(new BigDecimal("66.666666"), result));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
