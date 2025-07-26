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
        return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance, 1);
    }

    private BigDecimal injectPairByGreaterZeroBalance(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance, int surtimes) {
        var usdCurrencyBalance = getUsdCurrency();
        var japanCurrencyBalance = getJapanCurrency();
        var usdCcuBalance = getUsdCcu();
        var japanCcuBalance = getJapanCcu();
        var obtainUsdCcuBalance = sourceUsdCurrencyBalance.divide(usdCurrencyBalance, 6, RoundingMode.FLOOR).multiply(usdCcuBalance).setScale(6, RoundingMode.FLOOR);
        var obtainJapanCcuBalance = sourceJapanCurrencyBalance.divide(japanCurrencyBalance, 6, RoundingMode.FLOOR).multiply(japanCcuBalance).setScale(6, RoundingMode.FLOOR);

        if (ObjectUtil.equals(surtimes, 0)) {
            var obtainCcuBalanceEachSide = obtainUsdCcuBalance.min(obtainJapanCcuBalance);
            var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
            return obtainCcuBalance;
        }

        if (obtainJapanCcuBalance.compareTo(obtainUsdCcuBalance) > 0) {
            var rate = japanCurrencyBalance.add(sourceJapanCurrencyBalance).divide(usdCurrencyBalance.add(sourceUsdCurrencyBalance), 6, RoundingMode.FLOOR);
            var lackCcuBalance = japanCcuBalance.subtract(usdCcuBalance.multiply(rate)).divide(rate.add(new BigDecimal(1)), 6, RoundingMode.FLOOR);
            var exchangeJapanCurrencyBalance = lackCcuBalance.multiply(japanCurrencyBalance).divide(japanCcuBalance, 6, RoundingMode.FLOOR).divide(new BigDecimal(1).subtract(lackCcuBalance.divide(japanCcuBalance, 6, RoundingMode.FLOOR)), 6, RoundingMode.FLOOR);
            var exchangeUsdCurrencyBalance = exchange(japan, exchangeJapanCurrencyBalance);
            return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance.add(exchangeUsdCurrencyBalance), sourceJapanCurrencyBalance.subtract(exchangeJapanCurrencyBalance), 0);
        }

        if (obtainUsdCcuBalance.compareTo(obtainJapanCcuBalance) > 0) {
            var rate = usdCurrencyBalance.add(sourceUsdCurrencyBalance).divide(japanCurrencyBalance.add(sourceJapanCurrencyBalance), 6, RoundingMode.FLOOR);
            var lackCcuBalance = usdCcuBalance.subtract(japanCcuBalance.multiply(rate)).divide(rate.add(new BigDecimal(1)), 6, RoundingMode.FLOOR);
            var exchangeUsdCurrencyBalance = lackCcuBalance.multiply(usdCurrencyBalance).divide(usdCcuBalance, 6, RoundingMode.FLOOR).divide(new BigDecimal(1).subtract(lackCcuBalance.divide(usdCcuBalance, 6, RoundingMode.FLOOR)), 6, RoundingMode.FLOOR);
            var exchangeJapanCurrencyBalance = exchange(usd, exchangeUsdCurrencyBalance);
            return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance.subtract(exchangeUsdCurrencyBalance), sourceJapanCurrencyBalance.add(exchangeJapanCurrencyBalance), 0);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
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
