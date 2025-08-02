package com.john.project.test.common.lumen;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.Test;

public class LumenMathTest extends BaseTest {

    // usdCurrencyBalance = "66.66"
    private final String usdCurrencyBalance = "usdCurrencyBalance";
    // japanCurrencyBalance = "200"
    private final String japanCurrencyBalance = "japanCurrencyBalance";
    // usdCcuBalance = "150";
    private final String usdCcuBalance = "usdCcuBalance";
    // japanCcuBalance = "50";
    private final String japanCcuBalance = "japanCcuBalance";
    // injectUsdCurrencyBalance = "233.33"
    private final String injectUsdCurrencyBalance = "injectUsdCurrencyBalance";
    // injectJapanCurrencyBalance = "100"
    private final String injectJapanCurrencyBalance = "injectJapanCurrencyBalance";
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
        var mathBracketModel = init();
        "".toString();
    }

    private MathBracketModel init() {
        // (injectUsdCurrencyBalance - amountNeedToExchangeUsdCurrencyBalance) * (usdCcuBalance - exchangeCcuBalance) / (usdCurrencyBalance + amountNeedToExchangeUsdCurrencyBalance) = (injectJapanCurrencyBalance + exchangeJapanCurrencyBalance) *  (japanCcuBalance + exchangeCcuBalance) / (japanCurrencyBalance - exchangeJapanCurrencyBalance)
        var mathBracketModel = new MathBracketModel()
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
        mathBracketModel.handleMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        mathBracketModel.handleMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCcuBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCurrencyBalance))
                .setCalculationSymbol(subtractSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(usdCurrencyBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
        );
        mathBracketModel.handleAddToMultiply(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCcuBalance))
                .setCalculationSymbol(addSymbol)
                .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
        );
        mathBracketModel.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(usdCcuBalance))
                ));
        // y * injectUsdCurrencyBalance * z
        mathBracketModel.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                ));
        // y * x * usdCcuBalance
        mathBracketModel.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(usdCcuBalance))
                ));
        // y * injectUsdCurrencyBalance * z
        mathBracketModel.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                ));
        // (y * x * z)
        mathBracketModel.handleAdd(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                ));
        // y * injectUsdCurrencyBalance * z =
        mathBracketModel.handleSubtract(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeJapanCurrencyBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectUsdCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(exchangeCcuBalance))
                ));
        // japanCcuBalance * injectJapanCurrencyBalance * usdCurrencyBalance
        mathBracketModel.handleSubtract(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCcuBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(usdCurrencyBalance))
                ));
        // z * injectJapanCurrencyBalance * usdCurrencyBalance
        mathBracketModel.handleSubtract(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeCcuBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(usdCurrencyBalance))
                ));
        // japanCcuBalance * injectJapanCurrencyBalance * x
        mathBracketModel.handleSubtract(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(japanCcuBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                ));
        // z * injectJapanCurrencyBalance * x
        mathBracketModel.handleSubtract(new MathBracketModel()
                .setChildOne(new MathBracketModel().setName(exchangeCcuBalance))
                .setCalculationSymbol(multiplySymbol)
                .setChildTwo(new MathBracketModel()
                        .setChildOne(new MathBracketModel().setName(injectJapanCurrencyBalance))
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(new MathBracketModel().setName(amountNeedToExchangeUsdCurrencyBalance))
                ));
        return mathBracketModel;
    }

}