package com.john.project.model;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import tools.jackson.databind.ObjectMapper;
import com.john.project.common.uuid.UUIDUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class LumenContextModel {

    private List<LumenCcuBalanceModel> ccuBalanceList;

    private List<LumenCcuBalanceModel> tempBalanceList;

    private LumenCurrencyModel usd;
    private LumenCurrencyModel japan;

    public BigDecimal inject(LumenCurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        checkBalanceGreaterThanZero();
        var sourceUsdCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(usd.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        var sourceJapanCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(japan.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        if (NumberUtil.isGreater(sourceUsdCurrencyBalance, BigDecimal.ZERO)) {
            var beforeCCU = getUsdCcu().add(getJapanCcu());
            var afterCCU = getUsdCurrency().add(sourceUsdCurrencyBalance).min(getJapanCurrency()).multiply(new BigDecimal("2"));
        }
        if (NumberUtil.isGreater(sourceJapanCurrencyBalance, BigDecimal.ZERO)) {
            var beforeCCU = getUsdCcu().add(getJapanCcu());


        }


//        return injectPairByGreaterZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
//
//        if (ObjectUtil.equals(remainingTimes, 0) || ObjectUtil.equals(obtainOneCcuBalance, obtainTwoCcuBalance)) {
//            var obtainCcuBalanceEachSide = obtainOneCcuBalance.min(obtainTwoCcuBalance);
//            var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
//            var obtainOneCcuBalanceOfLast = obtainCcuBalance.multiply(oneCcuBalance).divide(totalCcu, 6, RoundingMode.FLOOR);
//            var obtainTwoCcuBalanceOfLast = obtainCcuBalance.subtract(obtainOneCcuBalanceOfLast);
//            tempBalanceList.add(new LumenCcuBalanceModel()
//                    .setId(uuidUtil.v4())
//                    .setCurrency(injectOneCurrency)
//                    .setCurrencyBalance(injectOneCurrencyBalance)
//                    .setCcuBalance(obtainOneCcuBalanceOfLast));
//            tempBalanceList.add(new LumenCcuBalanceModel()
//                    .setId(uuidUtil.v4())
//                    .setCurrency(injectTwoCurrency)
//                    .setCurrencyBalance(injectTwoCurrencyBalance)
//                    .setCcuBalance(obtainTwoCcuBalanceOfLast));
//            return obtainCcuBalance;
//        }
        return BigDecimal.ZERO;
    }

    public BigDecimal injectPair(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        checkSourceCurrencyBalanceLessThanOrEqualZero(sourceUsdCurrencyBalance);
        checkSourceCurrencyBalanceLessThanOrEqualZero(sourceJapanCurrencyBalance);
        if (hasEqualToZero()) {
            return injectPairByZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
        }
        return inject(usd, sourceUsdCurrencyBalance).add(inject(japan, sourceJapanCurrencyBalance));
    }

    private BigDecimal injectPairByZeroBalance(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var obtainCcuBalanceEachSide = sourceUsdCurrencyBalance.min(sourceJapanCurrencyBalance);
        var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
        tempBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(sourceUsdCurrencyBalance)
                .setCcuBalance(obtainCcuBalanceEachSide));
        tempBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(sourceJapanCurrencyBalance)
                .setCcuBalance(obtainCcuBalanceEachSide));
        return obtainCcuBalance;
    }

    public BigDecimal withdrawal(LumenCurrencyModel targetCurrency, BigDecimal ccuBalance) {
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
    public List<LumenCcuBalanceModel> withdrawalPair(BigDecimal ccuBalance) {
        checkBalanceGreaterThanOrEqualToZero();
        checkCcuBalanceGreaterThanOrEqualZero(ccuBalance);
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var usdCcuBalance = getUsdCcu();
        var japanCcuBalance = getJapanCcu();
        var usdCurrencyBalance = getUsdCurrency();
        var japanCurrencyBalance = getJapanCurrency();
        var obtainUsdCcuBalance = ccuBalance.multiply(usdCcuBalance).divide(usdCcuBalance.add(japanCcuBalance), 6, RoundingMode.FLOOR);
        var obtainJapanCcuBalance = ccuBalance.multiply(japanCcuBalance).divide(usdCcuBalance.add(japanCcuBalance), 6, RoundingMode.FLOOR);
        var obtainUsdCurrencyBalance = usdCurrencyBalance.multiply(obtainUsdCcuBalance).divide(usdCcuBalance, 6, RoundingMode.FLOOR);
        var obtainJapanCurrencyBalance = japanCurrencyBalance.multiply(obtainJapanCcuBalance).divide(japanCcuBalance, 6, RoundingMode.FLOOR);
        var obtainList = new ArrayList<LumenCcuBalanceModel>();
        obtainList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(obtainUsdCurrencyBalance)
                .setCcuBalance(obtainUsdCcuBalance.multiply(new BigDecimal(-1))));
        obtainList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(obtainJapanCurrencyBalance)
                .setCcuBalance(obtainJapanCcuBalance.multiply(new BigDecimal(-1))));
        tempBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(obtainUsdCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainUsdCcuBalance.multiply(new BigDecimal(-1))));
        tempBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(obtainJapanCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainJapanCcuBalance.multiply(new BigDecimal(-1))));
        checkBalanceGreaterThanOrEqualToZero();
        return obtainList;
    }

    public BigDecimal exchange(LumenCurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        var targetCurrency = JinqStream.from(
                        List.of(
                                usd,
                                japan
                        )
                )
                .where(s -> ObjectUtil.notEqual(sourceCurrency.getId(), s.getId()))
                .getOnlyValue();
        var obtainCCUOfSourceCurrency = inject(sourceCurrency, sourceBalance);
        var targetBalance = withdrawal(targetCurrency, obtainCCUOfSourceCurrency);
        return targetBalance;
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

    public LumenCcuBalanceModel combineBalance(LumenCurrencyModel currency) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var list = JinqStream.from(List.of(
                        ccuBalanceList,
                        tempBalanceList
                ))
                .selectAllList(s -> s)
                .where(s -> s.getCurrency().getId().equals(currency.getId()))
                .toList();
        var balance = new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(currency)
                .setCurrencyBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setCcuBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCcuBalance())).orElse(BigDecimal.ZERO));
        return balance;
    }

    public LumenContextModel() {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        usd = new LumenCurrencyModel()
                .setId(uuidUtil.v4())
                .setName("USD");
        japan = new LumenCurrencyModel()
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

    public void checkSourceCurrencyBalanceLessThanOrEqualZero(BigDecimal sourceCurrencyBalance) {
        if (NumberUtil.isLessOrEqual(sourceCurrencyBalance, BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
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
