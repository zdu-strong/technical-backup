package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenExchangeAndInjectTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var obtainJapan = this.lumenContext.exchange(lumenContext.getUsd(), new BigDecimal(100));
        var result = this.lumenContext.inject(lumenContext.getJapan(), new BigDecimal(100).add(obtainJapan));
        assertTrue(ObjectUtil.equals(new BigDecimal("199.999976"), result));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
