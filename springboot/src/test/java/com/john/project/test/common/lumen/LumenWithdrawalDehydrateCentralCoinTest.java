package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenWithdrawalDehydrateCentralCoinTest extends BaseTest {

    private LumenContextCoreModel lumenContextDehydrate;
    private BigDecimal obtainCcuBalanceFirst;

    @Test
    public void test() {
        var ratioFirst = this.lumenContextDehydrate.getTotalCcuBalance().divide(this.lumenContextDehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        var obtainJapanCurrencyBalanceFirst = this.lumenContextDehydrate.withdrawal(this.lumenContextDehydrate.getJapan(), obtainCcuBalanceFirst);
        var obtainCcuBalanceSecond = this.lumenContextDehydrate.inject(this.lumenContextDehydrate.getJapan(), obtainJapanCurrencyBalanceFirst);
        var ratioSecond = this.lumenContextDehydrate.getTotalCcuBalance().divide(this.lumenContextDehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        var obtainUsdCurrencyBalanceSecond = this.lumenContextDehydrate.withdrawal(this.lumenContextDehydrate.getUsd(), obtainCcuBalanceSecond);
        this.lumenContextDehydrate.inject(this.lumenContextDehydrate.getUsd(), obtainUsdCurrencyBalanceSecond);
        var ratioThird = this.lumenContextDehydrate.getTotalCcuBalance().divide(this.lumenContextDehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        this.lumenContextDehydrate.withdrawal(this.lumenContextDehydrate.getJapan(), new BigDecimal(10));
        var ratioFourth = this.lumenContextDehydrate.getTotalCcuBalance().divide(this.lumenContextDehydrate.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR);
        assertTrue(ObjectUtil.equals(new BigDecimal("2.249999"), ratioFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("2.166666"), ratioSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("2.142441"), ratioThird));
        assertTrue(ObjectUtil.equals(new BigDecimal("2.042441"), ratioFourth));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContextDehydrate = new LumenContextCoreModel();
        this.lumenContextDehydrate.injectPair(new BigDecimal(100), new BigDecimal(1000));
        this.obtainCcuBalanceFirst = this.lumenContextDehydrate.inject(this.lumenContextDehydrate.getJapan(), new BigDecimal(500));
    }

}
