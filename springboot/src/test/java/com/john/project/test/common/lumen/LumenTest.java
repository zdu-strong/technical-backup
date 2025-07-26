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
        var bb = lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

    @BeforeEach
    public void beforeEach() {
        lumenContext = new LumenContextModel();
    }

}
