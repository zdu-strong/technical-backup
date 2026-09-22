package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectTwiceTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var obtainCcuOfFirst = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        var obtainCcuOfSecond = this.lumenContext.inject(lumenContext.getJapan(), new BigDecimal(100));
        var result = obtainCcuOfFirst.add(obtainCcuOfSecond);
        assertTrue(ObjectUtil.equals(new BigDecimal("49.999999"), obtainCcuOfFirst));
        assertTrue(ObjectUtil.equals(new BigDecimal("100.000000"), obtainCcuOfSecond));
        assertTrue(ObjectUtil.equals(new BigDecimal("149.999999"), result));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
