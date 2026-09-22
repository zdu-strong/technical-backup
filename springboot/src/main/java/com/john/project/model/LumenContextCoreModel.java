package com.john.project.model;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class LumenContextCoreModel {

    private List<LumenCcuBalanceModel> ccuBalanceList;

    private LumenCurrencyModel usd;
    private LumenCurrencyModel japan;

    public BigDecimal injectPair(BigDecimal sourceUsdCurrencyBalanceForInput, BigDecimal targetJapanCurrencyBalanceForInput) {
        var obtainCcuBalance = getCcuBalanceBeGenerate(usd, sourceUsdCurrencyBalanceForInput, japan, targetJapanCurrencyBalanceForInput);

        this.addCcuToList(usd, sourceUsdCurrencyBalanceForInput, japan, targetJapanCurrencyBalanceForInput, obtainCcuBalance);

        return obtainCcuBalance;
    }

    @SneakyThrows
    public LumenCcuBalanceModel withdrawalPair(BigDecimal ccuBalance) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var usdCurrencyBalance = getUsdCurrencyBalance();
        var japanCurrencyBalance = getJapanCurrencyBalance();
        var totalCcuBalance = getTotalCcuBalance();

        checkLumenCcuBalance();
        checkCcuBalanceGreaterThanOrEqualZero(ccuBalance);

        var obtainUsdCurrencyBalance = usdCurrencyBalance.multiply(ccuBalance).divide(totalCcuBalance, 6, RoundingMode.FLOOR);
        var obtainJapanCurrencyBalance = japanCurrencyBalance.multiply(ccuBalance).divide(totalCcuBalance, 6, RoundingMode.FLOOR);

        this.addCcuToList(usd, obtainUsdCurrencyBalance.negate(), japan, obtainJapanCurrencyBalance.negate(), ccuBalance.negate());

        var obtainLumenCcuBalance = new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setUsdCurrencyBalance(obtainUsdCurrencyBalance)
                .setJapanCurrencyBalance(obtainJapanCurrencyBalance)
                .setCcuBalance(ccuBalance.negate());
        return obtainLumenCcuBalance;
    }

    public BigDecimal exchange(LumenCurrencyModel sourceCurrency, BigDecimal sourceCurrencyBalanceForExchange) {
        var obtainSourceCcuBalance = this.inject(sourceCurrency, sourceCurrencyBalanceForExchange);
        var targetCurrency = getTargetCurrency(sourceCurrency);
        return this.withdrawal(targetCurrency, obtainSourceCcuBalance);
    }

    public BigDecimal inject(LumenCurrencyModel sourceCurrency, BigDecimal sourceCurrencyBalanceForInject) {
        var sourceUsdCurrencyBalanceForInput = ObjectUtil.equals(usd.getId(), sourceCurrency.getId()) ? sourceCurrencyBalanceForInject : BigDecimal.ZERO;
        var targetJapanCurrencyBalanceForInput = ObjectUtil.equals(usd.getId(), sourceCurrency.getId()) ? BigDecimal.ZERO : sourceCurrencyBalanceForInject;
        return injectPair(sourceUsdCurrencyBalanceForInput, targetJapanCurrencyBalanceForInput);
    }

    public BigDecimal withdrawal(LumenCurrencyModel targetCurrency, BigDecimal ccuBalance) {
        var sourceCurrency = getTargetCurrency(targetCurrency);
        var totalCcuBalance = getTotalCcuBalance();
        var targetCurrencyBalance = ObjectUtil.equals(usd.getId(), targetCurrency.getId()) ? getUsdCurrencyBalance() : getJapanCurrencyBalance();

        checkLumenCcuBalance();
        checkCcuBalanceGreaterThanOrEqualZero(ccuBalance);

        if (NumberUtil.equals(ccuBalance, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        var obtainTargetCurrencyBalanceFirst = targetCurrencyBalance.multiply(ccuBalance).divide(ccuBalance.add(totalCcuBalance.divide(BigDecimal.TWO, 6, RoundingMode.FLOOR)), 6, RoundingMode.FLOOR);
        var obtainTargetCurrencyBalanceSecond = targetCurrencyBalance.multiply(ccuBalance).multiply(BigDecimal.TWO).divide(ccuBalance.add(totalCcuBalance), 6, RoundingMode.FLOOR);
        var obtainTargetCurrencyBalance = obtainTargetCurrencyBalanceFirst.max(obtainTargetCurrencyBalanceSecond);

        this.addCcuToList(sourceCurrency, BigDecimal.ZERO, targetCurrency, obtainTargetCurrencyBalance.negate(), ccuBalance.negate());

        return obtainTargetCurrencyBalance;
    }

    public BigDecimal getUsdCurrencyBalance() {
        var balance = this.combineBalance();
        return balance.getUsdCurrencyBalance();
    }

    public BigDecimal getJapanCurrencyBalance() {
        var balance = this.combineBalance();
        return balance.getJapanCurrencyBalance();
    }

    public BigDecimal getTotalCcuBalance() {
        var balance = this.combineBalance();
        return balance.getCcuBalance();
    }

    private BigDecimal getCcuBalanceBeGenerate(LumenCurrencyModel sourceCurrency, BigDecimal sourceCurrencyBalanceForInput, LumenCurrencyModel targetCurrency, BigDecimal targetCurrencyBalanceForInput) {
        var sourceCurrencyBalance = getCurrencyBalance(sourceCurrency);
        var targetCurrencyBalance = getCurrencyBalance(targetCurrency);
        var totalCcuBalance = getTotalCcuBalance();
        var ccuBalanceOfBase = sourceCurrencyBalance.add(sourceCurrencyBalanceForInput).min(targetCurrencyBalance.add(targetCurrencyBalanceForInput));
        var obtainSourceCcuBalance = BigDecimal.ZERO;
        var obtainTargetCcuBalance = BigDecimal.ZERO;
        var isSourceCurrencyAsCcuBase = NumberUtil.equals(sourceCurrencyBalance.add(sourceCurrencyBalanceForInput), ccuBalanceOfBase) && (NumberUtil.isGreater(sourceCurrencyBalance, targetCurrencyBalance) || !NumberUtil.equals(targetCurrencyBalance.add(targetCurrencyBalanceForInput), ccuBalanceOfBase));
        var isTargetCurrencyAsCcuBase = NumberUtil.equals(targetCurrencyBalance.add(targetCurrencyBalanceForInput), ccuBalanceOfBase) && (NumberUtil.isGreater(targetCurrencyBalance, sourceCurrencyBalance) || !NumberUtil.equals(sourceCurrencyBalance.add(sourceCurrencyBalanceForInput), ccuBalanceOfBase));

        checkLumenCcuBalance();
        checkSourceCurrencyBalanceGreaterOrEqualZero(sourceCurrencyBalanceForInput);
        checkSourceCurrencyBalanceGreaterOrEqualZero(targetCurrencyBalanceForInput);

        if (hasCcuBalanceEqualsToZero()) {
            return ccuBalanceOfBase.multiply(BigDecimal.TWO);
        }

        // Convert currency to CCU
        var obtainSourceCcuBalanceFirst = Optional.of(ccuBalanceOfBase.multiply(sourceCurrencyBalanceForInput).divide(sourceCurrencyBalance.add(sourceCurrencyBalanceForInput), 6, RoundingMode.FLOOR))
                .map(s -> new Pair<>(s, sourceCurrencyBalanceForInput.multiply(totalCcuBalance).divide(sourceCurrencyBalance.multiply(BigDecimal.TWO), 6, RoundingMode.FLOOR)))
                .map(s -> isSourceCurrencyAsCcuBase ? s.getTwo() : s.getOne().min(s.getTwo()))
                .get();
        var obtainTargetCcuBalanceFirst = Optional.of(ccuBalanceOfBase.multiply(targetCurrencyBalanceForInput).divide(targetCurrencyBalance.add(targetCurrencyBalanceForInput), 6, RoundingMode.FLOOR))
                .map(s -> new Pair<>(s, targetCurrencyBalanceForInput.multiply(totalCcuBalance).divide(targetCurrencyBalance.multiply(BigDecimal.TWO), 6, RoundingMode.FLOOR)))
                .map(s -> isTargetCurrencyAsCcuBase ? s.getTwo() : s.getOne().min(s.getTwo()))
                .get();

        if (NumberUtil.isLess(obtainTargetCcuBalanceFirst, obtainSourceCcuBalanceFirst)) {
            return getCcuBalanceBeGenerate(targetCurrency, targetCurrencyBalanceForInput, sourceCurrency, sourceCurrencyBalanceForInput);
        }

        if (NumberUtil.equals(obtainTargetCcuBalanceFirst, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        // Pairwise injection of equal amounts of CCU
        var obtainCcuBalanceEachSide = obtainSourceCcuBalanceFirst.min(obtainTargetCcuBalanceFirst);
        obtainSourceCcuBalance = obtainSourceCcuBalance.add(obtainCcuBalanceEachSide);
        obtainTargetCcuBalance = obtainTargetCcuBalance.add(obtainCcuBalanceEachSide);

        // Calculate CCU exceeding the pairwise ratio
        var obtainTargetCcuBalanceSecond = obtainTargetCcuBalanceFirst.subtract(obtainCcuBalanceEachSide);

        // Calculate the portion of CCU that is ready for immediate use.
        var obtainTargetCcuBalanceThird = BigDecimal.ZERO;
        if (NumberUtil.isGreater(obtainTargetCcuBalanceSecond, BigDecimal.ZERO)) {
            var obtainTargetCcuBalanceFourth = BigDecimal.ZERO;
            if (isTargetCurrencyAsCcuBase) {
                obtainTargetCcuBalanceFourth = totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance)
                        .subtract(targetCurrencyBalance.add(obtainTargetCcuBalanceSecond).multiply(BigDecimal.TWO))
                        .max(BigDecimal.ZERO)
                        .min(obtainTargetCcuBalanceSecond);
            } else {
                obtainTargetCcuBalanceFourth = ccuBalanceOfBase.multiply(BigDecimal.TWO)
                        .subtract(totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance))
                        .max(BigDecimal.ZERO)
                        .min(obtainTargetCcuBalanceSecond)
                        .min(totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance).divide(BigDecimal.TWO, 6, RoundingMode.FLOOR));
            }
            obtainTargetCcuBalanceThird = obtainTargetCcuBalanceSecond.subtract(obtainTargetCcuBalanceFourth).max(BigDecimal.ZERO);
            obtainTargetCcuBalance = obtainTargetCcuBalance.add(obtainTargetCcuBalanceFourth);
        }

        // Lock the maximum CCU value
        if (NumberUtil.isGreater(obtainTargetCcuBalanceThird, BigDecimal.ZERO)) {
            var obtainTargetCcuBalanceFifth = BigDecimal.ZERO;
            var surplusCcuBalance = BigDecimal.ZERO;
            if (isTargetCurrencyAsCcuBase) {
                surplusCcuBalance = totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance)
                        .divide(totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance).add(ccuBalanceOfBase.subtract(obtainTargetCcuBalanceThird.multiply(targetCurrencyBalance.multiply(BigDecimal.TWO)).divide(totalCcuBalance, 6, RoundingMode.FLOOR))), 6, RoundingMode.FLOOR)
                        .pow(2, MathContext.DECIMAL128)
                        .multiply(totalCcuBalance);
            } else {
                surplusCcuBalance = ccuBalanceOfBase.multiply(new BigDecimal(3))
                        .subtract(totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance))
                        .max(BigDecimal.ZERO)
                        .min(totalCcuBalance.add(obtainSourceCcuBalance).add(obtainTargetCcuBalance).divide(BigDecimal.TWO, 6, RoundingMode.FLOOR));
            }
            obtainTargetCcuBalanceFifth = surplusCcuBalance.multiply(obtainTargetCcuBalanceThird)
                    .divide(obtainTargetCcuBalanceThird.add(surplusCcuBalance), 6, RoundingMode.FLOOR);
            obtainTargetCcuBalance = obtainTargetCcuBalance.add(obtainTargetCcuBalanceFifth);
        }

        var obtainCcuBalance = obtainSourceCcuBalance.add(obtainTargetCcuBalance);
        return obtainCcuBalance;
    }

    private void addCcuToList(LumenCurrencyModel sourceCurrency, BigDecimal sourceCurrencyBalanceForInput, LumenCurrencyModel targetCurrency, BigDecimal targetCurrencyBalanceForInput, BigDecimal ccuBalance) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var isUsdOfSourceFirstCurrency = ObjectUtil.equals(usd.getId(), sourceCurrency.getId());

        ccuBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setUsdCurrencyBalance(isUsdOfSourceFirstCurrency ? sourceCurrencyBalanceForInput : targetCurrencyBalanceForInput)
                .setJapanCurrencyBalance(isUsdOfSourceFirstCurrency ? targetCurrencyBalanceForInput : sourceCurrencyBalanceForInput)
                .setCcuBalance(ccuBalance));
        checkLumenCcuBalance();
    }

    private LumenCurrencyModel getTargetCurrency(LumenCurrencyModel sourceCurrency) {
        var targetCurrency = JinqStream.from(
                        List.of(
                                usd,
                                japan
                        )
                )
                .where(s -> ObjectUtil.notEqual(sourceCurrency.getId(), s.getId()))
                .getOnlyValue();
        return targetCurrency;
    }

    private BigDecimal getCurrencyBalance(LumenCurrencyModel lumenCurrencyModel) {
        if (ObjectUtil.equals(usd.getId(), lumenCurrencyModel.getId())) {
            return getUsdCurrencyBalance();
        } else {
            return getJapanCurrencyBalance();
        }
    }

    private LumenCcuBalanceModel combineBalance() {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var balance = new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setUsdCurrencyBalance(Optional.ofNullable(JinqStream.from(ccuBalanceList).sumBigDecimal(s -> s.getUsdCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setJapanCurrencyBalance(Optional.ofNullable(JinqStream.from(ccuBalanceList).sumBigDecimal(s -> s.getJapanCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setCcuBalance(Optional.ofNullable(JinqStream.from(ccuBalanceList).sumBigDecimal(s -> s.getCcuBalance())).orElse(BigDecimal.ZERO));
        return balance;
    }

    private void checkCcuBalanceGreaterThanOrEqualZero(BigDecimal withdrawalCcuBalance) {
        if (NumberUtil.isLess(withdrawalCcuBalance, BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getTotalCcuBalance(), withdrawalCcuBalance)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    private void checkSourceCurrencyBalanceGreaterOrEqualZero(BigDecimal sourceCurrencyBalance) {
        if (NumberUtil.isLess(sourceCurrencyBalance, BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
    }

    private boolean hasCcuBalanceEqualsToZero() {
        this.checkBalanceGreaterThanOrEqualToZero();
        if (NumberUtil.isGreater(getUsdCurrencyBalance().add(getJapanCurrencyBalance()).add(getTotalCcuBalance()), BigDecimal.ZERO)) {
            this.checkBalanceGreaterThanZero();
            return false;
        } else {
            return true;
        }
    }

    private void checkBalanceGreaterThanOrEqualToZero() {
        if (NumberUtil.isLess(getUsdCurrencyBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getJapanCurrencyBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getTotalCcuBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    private void checkBalanceGreaterThanZero() {
        if (NumberUtil.isLessOrEqual(getUsdCurrencyBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (NumberUtil.isLessOrEqual(getJapanCurrencyBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (NumberUtil.isLessOrEqual(getTotalCcuBalance(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
    }

    private void checkLumenCcuBalance() {
        hasCcuBalanceEqualsToZero();
    }

    public LumenContextCoreModel() {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        usd = new LumenCurrencyModel()
                .setId(uuidUtil.v4())
                .setName("USD");
        japan = new LumenCurrencyModel()
                .setId(uuidUtil.v4())
                .setName("JAPAN");
        this.ccuBalanceList = new ArrayList<>();
    }

}
