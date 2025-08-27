package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenWithdrawalPairTest extends BaseTest {

    private LumenContextModel lumenContext;

    @Test
    public void test() {
        var currencyBalanceList = this.lumenContext.withdrawalPair(new BigDecimal(100));
        var usdCurrencyBalance = JinqStream.from(currencyBalanceList)
                .where(s -> ObjectUtil.equals(this.lumenContext.getUsd().getId(), s.getCurrency().getId()))
                .select(s -> s.getCurrencyBalance())
                .getOnlyValue();
        var japanCurrencyBalance = JinqStream.from(currencyBalanceList)
                .where(s -> ObjectUtil.equals(this.lumenContext.getJapan().getId(), s.getCurrency().getId()))
                .select(s -> s.getCurrencyBalance())
                .getOnlyValue();
        assertTrue(ObjectUtil.equals(new BigDecimal("50"), usdCurrencyBalance));
        assertTrue(ObjectUtil.equals(new BigDecimal("50"), japanCurrencyBalance));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
