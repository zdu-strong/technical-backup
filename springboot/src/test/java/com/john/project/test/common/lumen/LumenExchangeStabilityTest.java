package com.john.project.test.common.lumen;

import cn.hutool.core.util.NumberUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LumenExchangeStabilityTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var listOfExchangeToUsd = Flowable.range(1, 100)
                .map(s -> this.lumenContext.exchange(lumenContext.getJapan(), new BigDecimal(50)))
                .toList()
                .blockingGet();
        var listOfExchangeToJapan = Flowable.range(1, 100)
                .map(s -> this.lumenContext.exchange(lumenContext.getUsd(), new BigDecimal(100)))
                .toList()
                .blockingGet();
        var resultTen = this.lumenContext.exchange(lumenContext.getUsd(), new BigDecimal("2000000000000000000000000000000"));
        var resultOfMaxRatio = resultTen.divide(new BigDecimal("1000000000000"), 6, RoundingMode.FLOOR);
        var countOfStabilityExchangeToUsd = JinqStream.from(listOfExchangeToUsd)
                .where(s -> NumberUtil.equals(new BigDecimal("100"), s.setScale(5, RoundingMode.HALF_UP)))
                .count();
        var countOfStabilityExchangeToJapan = JinqStream.from(listOfExchangeToJapan)
                .where(s -> NumberUtil.equals(new BigDecimal("50"), s.setScale(5, RoundingMode.HALF_UP)))
                .count();
        assertEquals(new BigDecimal("333333333333.333332"), resultTen);
        assertEquals(new BigDecimal("0.333333"), resultOfMaxRatio);
        assertEquals(listOfExchangeToUsd.size(), countOfStabilityExchangeToUsd);
        assertEquals(listOfExchangeToJapan.size(), countOfStabilityExchangeToJapan);
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal("2000000000000"), new BigDecimal("1000000000000"));
    }

}
