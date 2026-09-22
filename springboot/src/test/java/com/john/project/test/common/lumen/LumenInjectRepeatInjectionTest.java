package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectRepeatInjectionTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var ratioFirst = this.lumenContext.getTotalCcuBalance().divide(this.lumenContext.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        var obtainUsdCurrencyBalance = this.lumenContext.withdrawal(this.lumenContext.getUsd(), new BigDecimal(10));
        this.lumenContext.inject(this.lumenContext.getUsd(), obtainUsdCurrencyBalance);
        var ratioSecond = this.lumenContext.getTotalCcuBalance().divide(this.lumenContext.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        assertTrue(ObjectUtil.equals(new BigDecimal("1.900000"), ratioFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("1.899878"), ratioSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("10.471204"), obtainUsdCurrencyBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(1000), new BigDecimal(5000));
        this.lumenContext.withdrawal(this.lumenContext.getJapan(), new BigDecimal(100));
    }

}

