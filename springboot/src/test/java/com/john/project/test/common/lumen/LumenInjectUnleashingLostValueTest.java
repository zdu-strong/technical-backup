package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectUnleashingLostValueTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var obtainJapanCurrencyBalance = this.lumenContext.exchange(lumenContext.getUsd(), new BigDecimal(1));
        var totalCcuBalanceFirst = this.lumenContext.getTotalCcuBalance();
        var japanCurrencyBalanceBalance = this.lumenContext.getJapanCurrencyBalance();
        var result = this.lumenContext.injectPair(new BigDecimal("198"), new BigDecimal(100).add(obtainJapanCurrencyBalance));
        var usdCurrencyBalance = this.lumenContext.getUsdCurrencyBalance();
        var japanCurrencyBalance = this.lumenContext.getJapanCurrencyBalance();
        var totalCcuBalanceSecond = this.lumenContext.getTotalCcuBalance();
        assertTrue(ObjectUtil.equals(new BigDecimal("31.999942"), obtainJapanCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("68.000058"), japanCurrencyBalanceBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("11.037736"), result));
        assertTrue(ObjectUtil.equals(new BigDecimal("200"), usdCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("200"), japanCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("2.000000"), totalCcuBalanceFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("13.037736"), totalCcuBalanceSecond));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(1), new BigDecimal(100));
    }

}

