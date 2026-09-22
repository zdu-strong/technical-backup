package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectCurbScarceCurrencyTest extends BaseTest {

    private LumenContextCoreModel lumenContext;
    private BigDecimal initialUsdCurrencyBalance;
    private BigDecimal initialJapanCurrencyBalance;

    @Test
    public void test() {
        var obtainCcuBalanceFirst = this.lumenContext.inject(lumenContext.getUsd(), this.initialUsdCurrencyBalance);
        var obtainCcuBalanceSecond = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var obtainCcuBalanceThird = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var obtainCcuBalanceFourth = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var obtainCcuBalanceFifth = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var obtainCcuBalanceSixth = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var obtainCcuBalanceSeventh = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100000));
        var usdCurrencyBalance = this.lumenContext.getUsdCurrencyBalance();
        var japanCurrencyBalance = this.lumenContext.getJapanCurrencyBalance();
        var totalCcuBalance = this.lumenContext.getTotalCcuBalance();
        var obtainCcuBalanceFirstRatio = obtainCcuBalanceFirst.divide(this.initialUsdCurrencyBalance, 6, RoundingMode.FLOOR);
        assertTrue(ObjectUtil.equals(new BigDecimal("470587737.024001"), obtainCcuBalanceFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("0.470587"), obtainCcuBalanceFirstRatio));
        assertTrue(ObjectUtil.equals(new BigDecimal("61759.637806"), obtainCcuBalanceSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("61758.093910"), obtainCcuBalanceThird));
        assertTrue(ObjectUtil.equals(new BigDecimal("61756.550128"), obtainCcuBalanceFourth));
        assertTrue(ObjectUtil.equals(new BigDecimal("61755.006444"), obtainCcuBalanceFifth));
        assertTrue(ObjectUtil.equals(new BigDecimal("61753.462894"), obtainCcuBalanceSixth));
        assertTrue(ObjectUtil.equals(new BigDecimal("61751.919460"), obtainCcuBalanceSeventh));
        assertTrue(ObjectUtil.equals(new BigDecimal("2000600000"), usdCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("1000000000000"), japanCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("2470958271.694643"), totalCcuBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.initialUsdCurrencyBalance = new BigDecimal(1000 * 1000 * 1000);
        this.initialJapanCurrencyBalance = this.initialUsdCurrencyBalance.multiply(new BigDecimal(1000));
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(initialUsdCurrencyBalance, initialJapanCurrencyBalance);
    }

}

