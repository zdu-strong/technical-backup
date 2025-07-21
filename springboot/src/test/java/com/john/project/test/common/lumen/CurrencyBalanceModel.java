package com.john.project.test.common.lumen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class CurrencyBalanceModel {

    private String id;

    private CurrencyModel currencyType;

    private BigDecimal balance;

}
