package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.uuid.UUIDUtil;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Tuple4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
public class LumenCoreUtil {

    @Autowired
    private UUIDUtil uuidUtil;

    public void injectPair(CcuBalanceModel origin, CcuBalanceModel temp, String sourceCurrency, BigDecimal sourceBalance, String targetCurrency, BigDecimal targetBalance){
        var balance = combineBalance(origin, temp);
        // sourceBalance / sourceTotalBalance * sourceCcus
        // targetBalance / targetTotalBalance * targetCcus
    }

    public void withdrawalPair(CcuBalanceModel origin, CcuBalanceModel temp, BigDecimal ccus){

    }

    public CcuBalanceModel exchange(CcuBalanceModel origin, CcuBalanceModel temp, String sourceCurrency, BigDecimal sourceBalance) {
        var balance = combineBalance(origin, temp);
        var isUsdOfSource = ObjectUtil.equals(sourceCurrency, CcuConstant.USD);
        var someBalance = JinqStream.of(balance)
                .select(s -> new Tuple4<>(
                        isUsdOfSource ? s.getUsdCcuBalance() : s.getJapanCcuBalance(),
                        isUsdOfSource ? s.getUsdCurrencyBalance() : s.getJapanCurrencyBalance(),
                        isUsdOfSource ? s.getJapanCcuBalance() : s.getUsdCcuBalance(),
                        isUsdOfSource ? s.getJapanCurrencyBalance() : s.getUsdCurrencyBalance()
                ))
                .getOnlyValue();
        var sourceCcu = sourceBalance.divide(someBalance.getTwo().add(sourceBalance), 6, RoundingMode.FLOOR).multiply(someBalance.getOne());
        var targetBalance = sourceCcu.divide(someBalance.getThree().add(sourceCcu), 6, RoundingMode.FLOOR).multiply(someBalance.getFour());
        var result = new CcuBalanceModel()
                .setId(this.uuidUtil.v4())
                .setUsdCurrencyBalance(isUsdOfSource ? sourceBalance : targetBalance.multiply(new BigDecimal(-1)))
                .setUsdCcuBalance(isUsdOfSource ? sourceCcu.multiply(new BigDecimal(-1)) : sourceCcu)
                .setJapanCurrencyBalance(isUsdOfSource ? targetBalance.multiply(new BigDecimal(-1)) : sourceBalance)
                .setJapanCcuBalance(isUsdOfSource ? sourceCcu : sourceCcu.multiply(new BigDecimal(-1)));
        return result;
    }

    public CcuBalanceModel exchange(CcuBalanceModel origin, String sourceCurrency, BigDecimal sourceBalance) {
        return exchange(origin, null, sourceCurrency, sourceBalance);
    }

    private CcuBalanceModel combineBalance(CcuBalanceModel origin, CcuBalanceModel temp) {
        if (ObjectUtil.isNotNull(temp)) {
            return combineBalance(List.of(origin));
        } else {
            return combineBalance(List.of(origin, temp));
        }
    }

    private CcuBalanceModel combineBalance(List<CcuBalanceModel> ccuList) {
        var balance = new CcuBalanceModel()
                .setId(this.uuidUtil.v4())
                .setUsdCurrencyBalance(Optional.ofNullable(JinqStream.from(ccuList).sumBigDecimal(m -> m.getUsdCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setUsdCcuBalance(Optional.ofNullable(JinqStream.from(ccuList).sumBigDecimal(m -> m.getUsdCcuBalance())).orElse(BigDecimal.ZERO))
                .setJapanCurrencyBalance(Optional.ofNullable(JinqStream.from(ccuList).sumBigDecimal(m -> m.getJapanCurrencyBalance())).orElse(BigDecimal.ZERO))
                .setJapanCcuBalance(Optional.ofNullable(JinqStream.from(ccuList).sumBigDecimal(m -> m.getJapanCcuBalance())).orElse(BigDecimal.ZERO));
        return balance;
    }

}
