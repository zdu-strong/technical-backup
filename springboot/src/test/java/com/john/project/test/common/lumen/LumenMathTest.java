package com.john.project.test.common.lumen;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.Test;

public class LumenMathTest extends BaseTest {

    private final String usdCurrencyBalance = "66.66";
    private final String japanCurrencyBalance = "200";
    private final String usdCcuBalance = "150";
    private final String japanCcuBalance = "50";
    private final String injectUsdCurrencyBalance = "233.33";
    private final String injectJapanCurrencyBalance = "100";
    private final String amountNeedToExchangeUsdCurrencyBalance = "x";

    // z = x * 150 / (x + 66.66) ccu
    private final String exchangeCcuBalance = "z";

    // y = z * 200 / (50 + z )
    private final String exchangeJapanCurrencyBalance = "y";

    public static final String equalSymbol = "=";
    public static final String addSymbol = "+";
    public static final String subtractSymbol = "-";
    public static final String multiplySymbol = "*";
    public static final String divideSymbol = "/";

    @Test
    public void test() {
        var aa = init();
        aa.handleMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        aa.handleMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCcuBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        aa.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCcuBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
        );
        aa.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(usdCcuBalance))
                ));
        "".toString();
    }

    private MathBracketModel init() {
        // (injectUsdCurrencyBalance - amountNeedToExchangeUsdCurrencyBalance) * (usdCcuBalance - exchangeCcuBalance) / (usdCurrencyBalance + amountNeedToExchangeUsdCurrencyBalance) = (injectJapanCurrencyBalance + exchangeJapanCurrencyBalance) *  (japanCcuBalance + exchangeCcuBalance) / (japanCurrencyBalance - exchangeJapanCurrencyBalance)
        return new MathBracketModel()
                .setChildOne(
                        new MathBracketModel()
                                .setChildOne(
                                        new MathBracketModel()
                                                .setChildOne(
                                                        new MathBracketModel()
                                                                .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                                                                .setCalculationSymbol(subtractSymbol)
                                                                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                                                )
                                                .setCalculationSymbol(multiplySymbol)
                                                .setChildTwo(
                                                        new MathBracketModel()
                                                                .setChildOne(new MathBracketModel().setName(usdCcuBalance))
                                                                .setCalculationSymbol(subtractSymbol)
                                                                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                                                )
                                )
                                .setCalculationSymbol(divideSymbol)
                                .setChildTwo(
                                        new MathBracketModel()
                                                .setChildOne(new MathBracketModel().setName(usdCurrencyBalance))
                                                .setCalculationSymbol(addSymbol)
                                                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                                )
                )
                .setCalculationSymbol(equalSymbol)
                .setChildTwo(
                        new MathBracketModel()
                                .setChildOne(
                                        new MathBracketModel()
                                                .setChildOne(
                                                        new MathBracketModel()
                                                                .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                                                                .setCalculationSymbol(addSymbol)
                                                                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                                                )
                                                .setCalculationSymbol(multiplySymbol)
                                                .setChildTwo(
                                                        new MathBracketModel()
                                                                .setChildOne(new MathBracketModel().setName(japanCcuBalance))
                                                                .setCalculationSymbol(addSymbol)
                                                                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                                                )
                                )
                                .setCalculationSymbol(divideSymbol)
                                .setChildTwo(
                                        new MathBracketModel()
                                                .setChildOne(new MathBracketModel().setName(japanCurrencyBalance))
                                                .setCalculationSymbol(subtractSymbol)
                                                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                                )
                );
    }

}