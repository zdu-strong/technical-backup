package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var result = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        var obtainUsdCurrencyBalance = this.lumenContext.withdrawal(lumenContext.getUsd(), result);
        assertTrue(ObjectUtil.equals(new BigDecimal("33.333333"), result));
        assertTrue(ObjectUtil.equals(new BigDecimal("49.999999"), obtainUsdCurrencyBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
