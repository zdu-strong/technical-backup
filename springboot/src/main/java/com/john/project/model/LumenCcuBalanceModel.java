package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class LumenCcuBalanceModel {

    private String id;

    private BigDecimal usdCurrencyBalance;

    private BigDecimal ccuBalance;

    private BigDecimal japanCurrencyBalance;

}
