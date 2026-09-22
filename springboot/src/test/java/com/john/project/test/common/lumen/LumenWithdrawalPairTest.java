package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.LumenContextCoreModel;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LumenWithdrawalPairTest extends BaseTest {

    private LumenContextCoreModel lumenContext;

    @Test
    public void test() {
        var obtainLumenCcuBalance = this.lumenContext.withdrawalPair(new BigDecimal(100));
        assertTrue(ObjectUtil.equals(new BigDecimal("50"), obtainLumenCcuBalance.getUsdCurrencyBalance()));
        assertTrue(ObjectUtil.equals(new BigDecimal("50"), obtainLumenCcuBalance.getJapanCurrencyBalance()));
    }

    @BeforeEach
    public void beforeEach() {
        this.lumenContext = new LumenContextCoreModel();
        this.lumenContext.injectPair(new BigDecimal(100), new BigDecimal(100));
    }

}
