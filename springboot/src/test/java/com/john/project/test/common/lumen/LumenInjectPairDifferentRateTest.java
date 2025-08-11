package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectPairDifferentRateTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var result = this.lumenContext.injectPair(new BigDecimal(200), new BigDecimal(100));
        var usdCcuBalance = this.lumenContext.getUsdCcu();
        var japanCcuBalance = this.lumenContext.getJapanCcu();
        assertTrue(ObjectUtil.equals(new BigDecimal("399.999984"), result));
//        assertTrue(ObjectUtil.equals(new BigDecimal("400"), usdCcuBalance));
//        assertTrue(ObjectUtil.equals(new BigDecimal("400"), japanCcuBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(200));
        var usdCcuBalance = this.lumenContext.getUsdCcu();
        var japanCcuBalance = this.lumenContext.getJapanCcu();
        assertTrue(ObjectUtil.equals(new BigDecimal("200"), usdCcuBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("200"), japanCcuBalance));
    }

}
