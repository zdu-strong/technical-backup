package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.john.project.common.uuid.UUIDUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class LumenContextModel {

    private List<CcuBalanceModel> ccuBalanceList;

    private List<CcuBalanceModel> tempBalanceList;

    private CurrencyModel usd;
    private CurrencyModel japan;

    public BigDecimal inject(CurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        checkBalanceGreaterThanZero();
        var sourceUsdCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(usd.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        var sourceJapanCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(japan.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
    }

    public BigDecimal injectPair(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        if (sourceUsdCurrencyBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (sourceJapanCurrencyBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (hasEqualToZero()) {
            return injectPairByZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
        } else {
            return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
        }
    }

    private BigDecimal injectPairByZeroBalance(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var obtainCcuBalanceEachSide = sourceUsdCurrencyBalance.max(sourceJapanCurrencyBalance);
        var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(sourceUsdCurrencyBalance)
                .setCcuBalance(obtainCcuBalanceEachSide));
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(sourceJapanCurrencyBalance)
                .setCcuBalance(obtainCcuBalanceEachSide));
        return obtainCcuBalance;
    }

    private BigDecimal injectPairByGreaterZeroBalance(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        return injectPairByGreaterZeroBalance(usd, sourceUsdCurrencyBalance, japan, sourceJapanCurrencyBalance, 1);
    }

    private BigDecimal injectPairByGreaterZeroBalance(CurrencyModel injectOneCurrency, BigDecimal injectOneCurrencyBalance, CurrencyModel injectTwoCurrency, BigDecimal injectTwoCurrencyBalance, int remainingTimes) {
        if (injectOneCurrencyBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (injectTwoCurrencyBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (
                !JinqStream.from(
                                List.of(
                                        usd,
                                        japan
                                )
                        )
                        .where(s -> ObjectUtil.equals(s.getId(), injectOneCurrency.getId()))
                        .exists()

        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (
                !JinqStream.from(
                                List.of(
                                        usd,
                                        japan
                                )
                        )
                        .where(s -> ObjectUtil.equals(s.getId(), injectTwoCurrency.getId()))
                        .exists()

        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (ObjectUtil.equals(injectOneCurrency.getId(), injectTwoCurrency.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }

        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var usdCurrencyBalance = getUsdCurrency();
        var japanCurrencyBalance = getJapanCurrency();
        var usdCcuBalance = getUsdCcu();
        var japanCcuBalance = getJapanCcu();
        var isUsd = ObjectUtil.equals(usd.getId(), injectOneCurrency.getId());
        var oneCurrencyBalance = isUsd ? usdCurrencyBalance : japanCurrencyBalance;
        var twoCurrencyBalance = isUsd ? japanCurrencyBalance : usdCurrencyBalance;
        var oneCcuBalance = isUsd ? usdCcuBalance : japanCcuBalance;
        var twoCcuBalance = isUsd ? japanCcuBalance : usdCcuBalance;

        var obtainOneCcuBalance = injectOneCurrencyBalance.divide(oneCurrencyBalance, 6, RoundingMode.FLOOR).multiply(oneCcuBalance).setScale(6, RoundingMode.FLOOR);
        var obtainTwoCcuBalance = injectTwoCurrencyBalance.divide(twoCurrencyBalance, 6, RoundingMode.FLOOR).multiply(twoCcuBalance).setScale(6, RoundingMode.FLOOR);

        if (ObjectUtil.equals(remainingTimes, 0) || ObjectUtil.equals(obtainOneCcuBalance, obtainTwoCcuBalance)) {
            var obtainCcuBalanceEachSide = obtainOneCcuBalance.min(obtainTwoCcuBalance);
            var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
            tempBalanceList.add(new CcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(injectOneCurrency)
                    .setCurrencyBalance(injectOneCurrencyBalance)
                    .setCcuBalance(obtainOneCcuBalance));
            tempBalanceList.add(new CcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(injectTwoCurrency)
                    .setCurrencyBalance(injectTwoCurrencyBalance)
                    .setCcuBalance(obtainTwoCcuBalance));
            return obtainCcuBalance;
        }

        if (obtainOneCcuBalance.compareTo(injectTwoCurrencyBalance) > 0) {
            var amountNeedToExchangeOfOne = getAmountNeedToExchange(injectOneCurrency, injectOneCurrencyBalance, injectTwoCurrency, injectTwoCurrencyBalance);
            var exchangeCurrencyBalanceOfTwo = exchange(injectOneCurrency, amountNeedToExchangeOfOne);
            return injectPairByGreaterZeroBalance(injectOneCurrency, injectOneCurrencyBalance.subtract(amountNeedToExchangeOfOne), injectTwoCurrency, injectTwoCurrencyBalance.add(exchangeCurrencyBalanceOfTwo), 0);
        }

        if (obtainTwoCcuBalance.compareTo(injectOneCurrencyBalance) > 0) {
            return injectPairByGreaterZeroBalance(injectTwoCurrency, injectTwoCurrencyBalance, injectOneCurrency, injectOneCurrencyBalance, 1);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
    }

    private BigDecimal getAmountNeedToExchange(CurrencyModel injectOneCurrency, BigDecimal injectOneCurrencyBalance, CurrencyModel injectTwoCurrency, BigDecimal injectTwoCurrencyBalance) {
        BigDecimal sourceUsdCurrencyBalance = BigDecimal.ZERO;
        BigDecimal sourceJapanCurrencyBalance = BigDecimal.ZERO;
        if (sourceUsdCurrencyBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (sourceJapanCurrencyBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var usdCurrencyBalance = getUsdCurrency();
        var japanCurrencyBalance = getJapanCurrency();
        var usdCcuBalance = getUsdCcu();
        var japanCcuBalance = getJapanCcu();
        var obtainUsdCcuBalance = sourceUsdCurrencyBalance.divide(usdCurrencyBalance, 6, RoundingMode.FLOOR).multiply(usdCcuBalance).setScale(6, RoundingMode.FLOOR);
        var obtainJapanCcuBalance = sourceJapanCurrencyBalance.divide(japanCurrencyBalance, 6, RoundingMode.FLOOR).multiply(japanCcuBalance).setScale(6, RoundingMode.FLOOR);

        // 66.66 usdCurrencyBalance 200japanCurrencyBalance
        // 150 usdCcuBalance          50 japanCcuBalance

        // 233.33 injectUsdCurrencyBalance, 100 injectJapanCurrencyBalance
        // x usdCurrencyBalance      y japanCurrencyBalance
        // - x / (x + 66.66) * 150 ccu         + x / (x + 66.66) * 150 ccu
        // + x usdCurrencyBalance              - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200  japanCurrencyBalance
        // 池中ccu:  150 - x / (x + 66.66) * 150 usdCcu       50 +  x / (x + 66.66) * 150 japanCcu
        // 池中货币:  66.66 + x  usdCurrencyBalance          200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200  japanCurrencyBalance
        // 233.33 -x injectUsdCurrencyBalance        100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) injectJapanCurrencyBalance
        // (233.33 - x) / ( 66.66 + x ) * ( 150 - x / (x + 66.66) * 150 )
        // ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 )
        // (233.33 - x) / ( 66.66 + x ) * ( 150 - x / (x + 66.66) * 150 ) = ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 )
        // (233.33 - x)  * ( 150 - x / (x + 66.66) * 150 ) = ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 ) * ( 66.66 + x )
        // (233.33 - x)  =  ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 ) * ( 66.66 + x ) / ( 150 - x / (x + 66.66) * 150 )
        // 233.33 = ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 ) * ( 66.66 + x ) / ( 150 - x / (x + 66.66) * 150 )  + x
        // ( 100 + (  (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) ) / ( 200 - (x / (x + 66.66) * 150 ) / (50 + x / (x + 66.66) * 150 ) * 200 ) * ( 50 +  x / (x + 66.66) * 150 ) * ( 66.66 + x ) / ( 150 - x / (x + 66.66) * 150 )  + x = 233.33
        // x usdCurrencyBalance
        // y = z / (50 + z ) * 200  japanCurrencyBalance
        // z = x / (x + 66.66) * 150 ccu
        // ( 100 + y ) / ( 200 - y ) * ( 50 +  z ) * ( 66.66 + x ) / ( 150 - z )  + x = 233.33
        // 233.33 - x = ( 100 + y ) / ( 200 - y ) * ( 50 +  z ) * ( 66.66 + x ) / ( 150 - z )
        // (233.33 - x) * (200 - y) *  ( 150 - z ) = ( 100 + y ) * ( 50 +  z ) * ( 66.66 + x )
        return BigDecimal.ZERO;
    }

    public BigDecimal withdrawal(CurrencyModel targetCurrency, BigDecimal ccuBalance) {
        var balanceList = withdrawalPair(ccuBalance);
        checkBalanceGreaterThanZero();
        var sourceCurrency = JinqStream.from(
                        List.of(usd, japan)
                )
                .where(s -> ObjectUtil.notEqual(s.getId(), targetCurrency.getId()))
                .getOnlyValue();
        var sourceCurrencyBalance = Optional.ofNullable(JinqStream.from(balanceList)
                        .where(s -> ObjectUtil.equals(s.getCurrency().getId(), sourceCurrency.getId()))
                        .sumBigDecimal(s -> s.getCurrencyBalance()))
                .orElse(BigDecimal.ZERO);
        var obtainExchangeBalance = exchange(sourceCurrency, sourceCurrencyBalance);
        var obtainTargetCurrencyBalance = obtainExchangeBalance.add(
                Optional.ofNullable(
                        JinqStream.from(balanceList)
                                .where(s -> ObjectUtil.equals(s.getCurrency().getId(), targetCurrency.getId()))
                                .sumBigDecimal(s -> s.getCurrencyBalance())
                ).orElse(BigDecimal.ZERO)
        );
        return obtainTargetCurrencyBalance;
    }

    @SneakyThrows
    public List<CcuBalanceModel> withdrawalPair(BigDecimal ccuBalance) {
        checkBalanceGreaterThanOrEqualToZero();
        checkCcuBalanceGreaterThanOrEqualZero(ccuBalance);
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var usdCcuBalance = getUsdCcu();
        var japanCcuBalance = getJapanCcu();
        var usdCurrencyBalance = getUsdCurrency();
        var japanCurrencyBalance = getJapanCurrency();
        var obtainUsdCcuBalance = ccuBalance.multiply(usdCcuBalance.divide(usdCcuBalance.add(japanCcuBalance), 6, RoundingMode.FLOOR));
        var obtainJapanCcuBalance = ccuBalance.multiply(japanCcuBalance.divide(usdCcuBalance.add(japanCcuBalance), 6, RoundingMode.FLOOR));
        var obtainUsdCurrencyBalance = usdCurrencyBalance.multiply(obtainUsdCcuBalance.divide(usdCcuBalance, 6, RoundingMode.FLOOR));
        var obtainJapanCurrencyBalance = japanCurrencyBalance.multiply(obtainJapanCcuBalance.divide(japanCcuBalance, 6, RoundingMode.FLOOR));
        var obtainList = new ArrayList<CcuBalanceModel>();
        obtainList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(obtainUsdCurrencyBalance)
                .setCcuBalance(obtainUsdCcuBalance.multiply(new BigDecimal(-1))));
        obtainList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(obtainJapanCurrencyBalance)
                .setCcuBalance(obtainJapanCcuBalance.multiply(new BigDecimal(-1))));
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(obtainUsdCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainUsdCcuBalance.multiply(new BigDecimal(-1))));
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(obtainJapanCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainJapanCcuBalance.multiply(new BigDecimal(-1))));
        checkBalanceGreaterThanOrEqualToZero();
        return obtainList;
    }

    public BigDecimal exchange(CurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        checkBalanceGreaterThanZero();
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var targetCurrency = JinqStream.from(
                        List.of(
                                usd,
                                japan
                        )
                )
                .where(s -> ObjectUtil.notEqual(sourceCurrency.getId(), s.getId()))
                .getOnlyValue();
        var sourceCurrencyBalance = combineBalance(sourceCurrency).getCurrencyBalance();
        var sourceCcuBalance = combineBalance(sourceCurrency).getCcuBalance();
        var targetCurrencyBalance = combineBalance(targetCurrency).getCurrencyBalance();
        var targetCcuBalance = combineBalance(targetCurrency).getCcuBalance();
        var obtainSourceCcu = sourceBalance.divide(sourceBalance.add(sourceCurrencyBalance), 6, RoundingMode.FLOOR).multiply(sourceCcuBalance);
        var obtainTargetBalance = obtainSourceCcu.divide(obtainSourceCcu.add(targetCcuBalance), 6, RoundingMode.FLOOR).multiply(targetCurrencyBalance);
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(sourceCurrency)
                .setCurrencyBalance(sourceBalance)
                .setCcuBalance(obtainSourceCcu.multiply(new BigDecimal(-1))));
        tempBalanceList.add(new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(targetCurrency)
                .setCurrencyBalance(obtainTargetBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainSourceCcu));
        return obtainTargetBalance;
    }

    public BigDecimal getUsdCurrency() {
        var balance = this.combineBalance(usd);
        return balance.getCurrencyBalance();
    }

    public BigDecimal getUsdCcu() {
        var balance = this.combineBalance(usd);
        return balance.getCcuBalance();
    }

    public BigDecimal getJapanCurrency() {
        var balance = this.combineBalance(japan);
        return balance.getCurrencyBalance();
    }

    public BigDecimal getJapanCcu() {
        var balance = this.combineBalance(japan);
        return balance.getCcuBalance();
    }

    public CcuBalanceModel combineBalance(CurrencyModel currency) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var list = JinqStream.from(List.of(
                        ccuBalanceList,
                        tempBalanceList
                ))
                .selectAllList(s -> s)
                .where(s -> s.getCurrency().getId().equals(currency.getId()))
                .toList();
        var balance = new CcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(currency)
                .setCurrencyBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setCcuBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCcuBalance())).orElse(BigDecimal.ZERO));
        return balance;
    }

    public LumenContextModel() {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        usd = new CurrencyModel()
                .setId(uuidUtil.v4())
                .setName("USD");
        japan = new CurrencyModel()
                .setId(uuidUtil.v4())
                .setName("JAPAN");
        this.ccuBalanceList = new ArrayList<>();
        this.tempBalanceList = new ArrayList<>();
    }

    public void checkCcuBalanceGreaterThanOrEqualZero(BigDecimal withdrawalCcuBalance) {
        if (getUsdCcu().add(getJapanCcu()).compareTo(withdrawalCcuBalance) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    public void checkBalanceGreaterThanZero() {
        if (getUsdCurrency().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (getUsdCcu().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (getJapanCurrency().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (getJapanCcu().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
    }

    public void checkBalanceGreaterThanOrEqualToZero() {
        if (getUsdCurrency().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (getUsdCcu().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (getJapanCurrency().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (getJapanCcu().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    public boolean hasEqualToZero() {
        this.checkBalanceGreaterThanOrEqualToZero();
        if (getUsdCurrency().add(getUsdCcu()).add(getJapanCurrency()).add(getJapanCcu()).compareTo(BigDecimal.ZERO) > 0) {
            this.checkBalanceGreaterThanZero();
            return false;
        } else {
            return true;
        }
    }

}
