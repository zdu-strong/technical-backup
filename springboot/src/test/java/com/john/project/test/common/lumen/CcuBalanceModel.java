package com.john.project.test.common.lumen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class CcuBalanceModel {

    private String id;

    private CurrencyModel usdType;

    private BigDecimal usdCurrencyBalance;

    private BigDecimal usdCcuBalance;

    private CurrencyModel japanType;

    private BigDecimal japanCurrencyBalance;

    private BigDecimal japanCcuBalance;

}
