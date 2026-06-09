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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    public BigDecimal inject(LumenCurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        checkBalanceGreaterThanZero();
        var sourceUsdCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(usd.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        var sourceJapanCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(japan.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        if (NumberUtil.isGreater(sourceUsdCurrencyBalance, BigDecimal.ZERO)) {
            var obtainCcuBalance = getTargetCcu(sourceCurrency, sourceBalance);
            var obtainOneCcuBalanceOfLast = BigDecimal.ZERO;
            var obtainTwoCcuBalanceOfLast = obtainCcuBalance;
            var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(usd)
                    .setCurrencyBalance(sourceUsdCurrencyBalance)
                    .setCcuBalance(obtainOneCcuBalanceOfLast));
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(japan)
                    .setCurrencyBalance(sourceJapanCurrencyBalance)
                    .setCcuBalance(obtainTwoCcuBalanceOfLast));
            return obtainCcuBalance;
        }

        if (NumberUtil.isGreater(sourceJapanCurrencyBalance, BigDecimal.ZERO)) {
            var obtainOneCcuBalanceOfLast = getTargetCcu(sourceCurrency, sourceBalance);
            var obtainTwoCcuBalanceOfLast = BigDecimal.ZERO;
            var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(usd)
                    .setCurrencyBalance(sourceUsdCurrencyBalance)
                    .setCcuBalance(obtainOneCcuBalanceOfLast));
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(japan)
                    .setCurrencyBalance(sourceJapanCurrencyBalance)
                    .setCcuBalance(obtainTwoCcuBalanceOfLast));
            return obtainOneCcuBalanceOfLast;
        }



        return BigDecimal.ZERO;
    }

    public BigDecimal injectPair(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        checkSourceCurrencyBalanceGreaterZero(sourceUsdCurrencyBalance);
        checkSourceCurrencyBalanceGreaterZero(sourceJapanCurrencyBalance);
        if (hasEqualToZero()) {
            return injectPairByZeroBalance(sourceUsdCurrencyBalance, sourceJapanCurrencyBalance);
        }
        return inject(usd, sourceUsdCurrencyBalance).add(inject(japan, sourceJapanCurrencyBalance));
    }

    private BigDecimal injectPairByZeroBalance(BigDecimal sourceUsdCurrencyBalance, BigDecimal sourceJapanCurrencyBalance) {
        var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        var obtainCcuBalanceEachSide = sourceUsdCurrencyBalance.max(sourceJapanCurrencyBalance);
        var obtainCcuBalance = obtainCcuBalanceEachSide.multiply(new BigDecimal(2));
        ccuBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(sourceUsdCurrencyBalance)
                .setCcuBalance(obtainCcuBalanceEachSide));
        ccuBalanceList.add(new LumenCcuBalanceModel()
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
        ccuBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(usd)
                .setCurrencyBalance(obtainUsdCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainUsdCcuBalance.multiply(new BigDecimal(-1))));
        ccuBalanceList.add(new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(japan)
                .setCurrencyBalance(obtainJapanCurrencyBalance.multiply(new BigDecimal(-1)))
                .setCcuBalance(obtainJapanCcuBalance.multiply(new BigDecimal(-1))));
        checkBalanceGreaterThanOrEqualToZero();
        return obtainList;
    }

    public BigDecimal exchange(LumenCurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        checkBalanceGreaterThanZero();
        var sourceUsdCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(usd.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        var sourceJapanCurrencyBalance = Optional.of(sourceBalance).filter(s -> ObjectUtil.equals(japan.getId(), sourceCurrency.getId())).orElse(BigDecimal.ZERO);
        if (NumberUtil.isGreater(sourceUsdCurrencyBalance, BigDecimal.ZERO)) {
            var obtainCcuBalance = getTargetCcu(sourceCurrency, sourceBalance);
            var targetJapanCurrencyBalance = getJapanCurrency().multiply(obtainCcuBalance).divide(getJapanCcu().add(obtainCcuBalance), 6, RoundingMode.FLOOR);
            var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(usd)
                    .setCurrencyBalance(sourceUsdCurrencyBalance)
                    .setCcuBalance(obtainCcuBalance.multiply(new BigDecimal(-1))));
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(japan)
                    .setCurrencyBalance(targetJapanCurrencyBalance.multiply(new BigDecimal(-1)))
                    .setCcuBalance(obtainCcuBalance));
            return targetJapanCurrencyBalance;
        }

        if (NumberUtil.isGreater(sourceJapanCurrencyBalance, BigDecimal.ZERO)) {
            var obtainCcuBalance = getTargetCcu(sourceCurrency, sourceBalance);
            var targetUsdCurrencyBalance = getUsdCurrency().multiply(obtainCcuBalance).divide(getUsdCcu().add(obtainCcuBalance), 6, RoundingMode.FLOOR);
            var uuidUtil = SpringUtil.getBean(UUIDUtil.class);
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(usd)
                    .setCurrencyBalance(targetUsdCurrencyBalance.multiply(new BigDecimal(-1)))
                    .setCcuBalance(obtainCcuBalance));
            ccuBalanceList.add(new LumenCcuBalanceModel()
                    .setId(uuidUtil.v4())
                    .setCurrency(japan)
                    .setCurrencyBalance(sourceJapanCurrencyBalance)
                    .setCcuBalance(obtainCcuBalance.multiply(new BigDecimal(-1))));
            return obtainCcuBalance;
        }
        return BigDecimal.ZERO;
    }

    private LumenCurrencyModel getTargetCurrency(LumenCurrencyModel sourceCurrency){
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

    private BigDecimal getTargetCcu(LumenCurrencyModel sourceCurrency, BigDecimal sourceBalance) {
        var targetCurrency = getTargetCurrency(sourceCurrency);
        var sourceCurrencyBalance = combineBalance(sourceCurrency).getCurrencyBalance();
        var sourceCcuBalance = combineBalance(sourceCurrency).getCcuBalance();
        var targetCurrencyBalance = combineBalance(targetCurrency).getCurrencyBalance();
        var targetCcuBalance = combineBalance(targetCurrency).getCcuBalance();

//        var obtainSourceCcu = sourceBalance.multiply(sourceCcuBalance).divide(sourceBalance.add(sourceCurrencyBalance), 6, RoundingMode.FLOOR);
//        var obtainTargetBalance = obtainSourceCcu.multiply(targetCurrencyBalance).divide(obtainSourceCcu.add(targetCcuBalance), 6, RoundingMode.FLOOR);

        // 150美元 / (150 美元 * 2 + 150 美元 * 2) = 0.25
        // x美元 / (x 美元 * 2 + 150 美元 * 2) = 0.25
        // 100ccu * 2 / (100ccu * 2 + 300ccu + 300 japan ccu) = 0.25

        return BigDecimal.ZERO;

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
        var list = JinqStream.from(ccuBalanceList)
                .where(s -> s.getCurrency().getId().equals(currency.getId()))
                .toList();
        var balance = new LumenCcuBalanceModel()
                .setId(uuidUtil.v4())
                .setCurrency(currency)
                .setCurrencyBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setCcuBalance(Optional.ofNullable(JinqStream.from(list).sumBigDecimal(s -> s.getCcuBalance())).orElse(BigDecimal.ZERO));
        return balance;
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

    public void checkCcuBalanceGreaterThanOrEqualZero(BigDecimal withdrawalCcuBalance) {
        if (NumberUtil.isLess(getUsdCcu().add(getJapanCcu()), withdrawalCcuBalance)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    public void checkSourceCurrencyBalanceGreaterZero(BigDecimal sourceCurrencyBalance) {
        if (NumberUtil.isLessOrEqual(sourceCurrencyBalance, BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
    }

    public void checkBalanceGreaterThanZero() {
        if (NumberUtil.isLessOrEqual(getUsdCurrency(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (NumberUtil.isLessOrEqual(getUsdCcu(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (NumberUtil.isLessOrEqual(getJapanCurrency(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
        if (NumberUtil.isLessOrEqual(getJapanCcu(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance must greater than 0");
        }
    }

    public void checkBalanceGreaterThanOrEqualToZero() {
        if (NumberUtil.isLess(getUsdCurrency(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getUsdCcu(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getJapanCurrency(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
        if (NumberUtil.isLess(getJapanCcu(), BigDecimal.ZERO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "balance cannot less than 0");
        }
    }

    public boolean hasEqualToZero() {
        this.checkBalanceGreaterThanOrEqualToZero();
        if (NumberUtil.isGreater(getUsdCurrency().add(getUsdCcu()).add(getJapanCurrency()).add(getJapanCcu()), BigDecimal.ZERO)) {
            this.checkBalanceGreaterThanZero();
            return false;
        } else {
            return true;
        }
    }

}
