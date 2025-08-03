package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectThreeTimesTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var obtainCcuOfFirst = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(50));
        var obtainCcuOfSecond = this.lumenContext.inject(lumenContext.getJapan(), new BigDecimal(100));
        var obtainCcuOfThree = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(50));
        var result = obtainCcuOfFirst.add(obtainCcuOfSecond).add(obtainCcuOfThree);
        assertTrue(ObjectUtil.equals(new BigDecimal("199.998414"), result));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
