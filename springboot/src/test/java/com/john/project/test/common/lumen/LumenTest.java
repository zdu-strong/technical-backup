package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var aa = lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
        var bb = lumenContext.exchange(lumenContext.getJapan(), new BigDecimal(100));
        var cc = lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        var dd = lumenContext.inject(lumenContext.getJapan(), new BigDecimal(100));
        var ff = lumenContext.inject(lumenContext.getUsd(), bb);
        var gg = lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        assertTrue(ObjectUtil.equals(new BigDecimal("200"), aa));
//        assertTrue(cc.add(dd).add(ff).add(gg).compareTo(new BigDecimal("399")) > 0);
        "".toString();
    }

    @BeforeEach
    public void beforeEach() {
        lumenContext = new LumenContextModel();
    }

}
