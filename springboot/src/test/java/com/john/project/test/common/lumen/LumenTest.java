package com.john.project.test.common.lumen;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class LumenTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var aa = lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
        var bb = lumenContext.exchange(lumenContext.getJapan(), new BigDecimal(100));
        var cc = lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        var dd = lumenContext.inject(lumenContext.getJapan(), new BigDecimal(50));
        var ff = lumenContext.inject(lumenContext.getUsd(), bb);
        "".toString();
    }

    @BeforeEach
    public void beforeEach() {
        lumenContext = new LumenContextModel();
    }

}
