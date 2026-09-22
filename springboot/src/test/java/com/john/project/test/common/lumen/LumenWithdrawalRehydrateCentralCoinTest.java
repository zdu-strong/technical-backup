package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenWithdrawalRehydrateCentralCoinTest extends BaseTest {

    private LumenContextCoreModel lumenContextRehydrate;

    @Test
    public void test() {
        var ratioFirst = this.lumenContextRehydrate.getTotalCcuBalance().divide(this.lumenContextRehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        var obtainCcuBalanceSecond = this.lumenContextRehydrate.inject(this.lumenContextRehydrate.getJapan(), new BigDecimal(100));
        var ratioSecond = this.lumenContextRehydrate.getTotalCcuBalance().divide(this.lumenContextRehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        this.lumenContextRehydrate.withdrawal(this.lumenContextRehydrate.getUsd(), obtainCcuBalanceSecond);
        var ratioThird = this.lumenContextRehydrate.getTotalCcuBalance().divide(this.lumenContextRehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        this.lumenContextRehydrate.inject(this.lumenContextRehydrate.getJapan(), new BigDecimal("100"));
        var ratioFourth = this.lumenContextRehydrate.getTotalCcuBalance().divide(this.lumenContextRehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        assertTrue(ObjectUtil.equals(new BigDecimal("1.900000"), ratioFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("1.920999"), ratioSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("1.941999"), ratioThird));
        assertTrue(ObjectUtil.equals(new BigDecimal("1.962999"), ratioFourth));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContextRehydrate = new LumenContextCoreModel();
        this.lumenContextRehydrate.injectPair(new BigDecimal(1000), new BigDecimal(5000));
        this.lumenContextRehydrate.withdrawal(this.lumenContextRehydrate.getJapan(), new BigDecimal(100));
    }

}

