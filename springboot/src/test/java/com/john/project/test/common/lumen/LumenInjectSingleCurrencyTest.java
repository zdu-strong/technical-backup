package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenInjectSingleCurrencyTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var obtainCcuOfFirst = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(100));
        var obtainCcuOfSecond = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(200));
        var obtainCcuOfThree = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(400));
        var obtainCcuOfFour = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(800));
        var obtainCcuOfFive = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(1600));
        var obtainCcuOfSix = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(3200));
        var obtainCcuOfSeven = this.lumenContext.inject(lumenContext.getUsd(), new BigDecimal(6400));
        var result = JinqStream.from(List.of(
                        obtainCcuOfFirst,
                        obtainCcuOfSecond,
                        obtainCcuOfThree,
                        obtainCcuOfFour,
                        obtainCcuOfFive,
                        obtainCcuOfSix,
                        obtainCcuOfSeven
                ))
                .sumBigDecimal(s -> s);
        var obtainJapanCurrencyBalance = this.lumenContext.withdrawal(this.lumenContext.getJapan(), result);
        assertTrue(ObjectUtil.equals(new BigDecimal("196.899192"), result));
        assertTrue(ObjectUtil.equals(new BigDecimal("49.803916"), obtainJapanCurrencyBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
