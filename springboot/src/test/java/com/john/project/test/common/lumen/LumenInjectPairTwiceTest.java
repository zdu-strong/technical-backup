package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectPairTwiceTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var obtainCCUOfFirst = this.lumenContext.injectPair(new BigDecimal(50), new BigDecimal(100));
        var obtainCCUOfSecond = this.lumenContext.inject(this.lumenContext.getUsd(), new BigDecimal(150));
        var totalCCU = obtainCCUOfFirst.add(obtainCCUOfSecond);
        assertTrue(ObjectUtil.equals(new BigDecimal("100"), obtainCCUOfFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("150.000000"), obtainCCUOfSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("250.000000"), totalCCU));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(200));
        var totalCcuBalance = this.lumenContext.getTotalCcuBalance();
        assertTrue(ObjectUtil.equals(new BigDecimal("200.000000"), totalCcuBalance));
    }

}
