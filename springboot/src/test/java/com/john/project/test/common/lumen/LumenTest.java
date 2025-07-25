package com.john.project.test.common.lumen;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.test.common.BaseTest.BaseTest;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.jinq.tuples.Tuple4;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LumenTest extends BaseTest {
//
//    private CurrencyModel usd;
//
//    private CurrencyModel japan;
//
//    private CcuModel ccu;
//
//    private List<CcuBalanceModel> balanceList;
//
    @Test
    public void test() {
//        var ccus = injectPair(new BigDecimal(100), new BigDecimal(10000));
//        var firstInjectCcus = inject(usd, new BigDecimal(100));
//        var secondInjectCcus = inject(japan, new BigDecimal("10000"));
//        var obtainCcu = JinqStream.from(List.of(firstInjectCcus, secondInjectCcus)).sumBigDecimal(s -> s);
//        "".toString();
    }
//
//    private void injectPairBase(BigDecimal usdBalance, BigDecimal japanBalance){
//
//    }
//
//    private BigDecimal injectPair(BigDecimal usdBalance, BigDecimal japanBalance) {
//        var balance = getBalance();
//        if (balance.getUsdCcuBalance().compareTo(BigDecimal.ZERO) > 0) {
//            var totalCcu = balance.getUsdCcuBalance().add(balance.getJapanCcuBalance());
//            var halfCcu = totalCcu.divide(new BigDecimal(2), 6, RoundingMode.FLOOR);
//            var usdCcu = usdBalance.divide(balance.getUsdCurrencyBalance(), 6, RoundingMode.FLOOR).multiply(halfCcu);
//            var japanCcu = japanBalance.divide(balance.getJapanCurrencyBalance(), 6, RoundingMode.FLOOR).multiply(halfCcu);
//            var minCcu = usdCcu.min(japanCcu);
//            balanceList.add(new CcuBalanceModel()
//                    .setId(this.uuidUtil.v4())
//                    .setUsdType(usd)
//                    .setUsdCurrencyBalance(usdBalance)
//                    .setUsdCcuBalance(minCcu)
//                    .setJapanType(japan)
//                    .setJapanCurrencyBalance(japanBalance)
//                    .setJapanCcuBalance(minCcu));
//            return minCcu.multiply(new BigDecimal(2));
//        } else {
//            var minCcu = usdBalance.add(japanBalance).divide(new BigDecimal(2), 6, RoundingMode.FLOOR);
//            balanceList.add(new CcuBalanceModel()
//                    .setId(this.uuidUtil.v4())
//                    .setUsdType(usd)
//                    .setUsdCurrencyBalance(usdBalance)
//                    .setUsdCcuBalance(minCcu)
//                    .setJapanType(japan)
//                    .setJapanCurrencyBalance(japanBalance)
//                    .setJapanCcuBalance(minCcu));
//            return minCcu.multiply(new BigDecimal(2));
//        }
//    }
//
//    private BigDecimal inject(CurrencyModel sourceCurrency, BigDecimal sourceBalance) {
//        var halfCurrency = sourceBalance.divide(new BigDecimal("2"), 6, RoundingMode.FLOOR);
//        var targetCurrencyBalance = exchange(sourceCurrency, halfCurrency, JinqStream.from(List.of(usd, japan)).where(s -> !ObjectUtil.equals(sourceCurrency.getId(), s.getId())).getOnlyValue());
//        var usdBalance = JinqStream.of(usd).where(s -> ObjectUtil.equals(sourceCurrency.getId(), s.getId())).select(s -> halfCurrency).findFirst().orElse(targetCurrencyBalance);
//        var japanBalance = JinqStream.of(japan).where(s -> ObjectUtil.equals(sourceCurrency.getId(), s.getId())).select(s -> halfCurrency).findFirst().orElse(targetCurrencyBalance);
//        return injectPair(usdBalance, japanBalance);
//    }
//
//    private void withdrawal(CurrencyModel targetCurrency, BigDecimal ccuBalance) {
//
//    }
//
//    private BigDecimal exchange(CurrencyModel sourceCurrency, BigDecimal sourceBalance, CurrencyModel targetCurrency) {
//        var balance = getBalance();
//        if (ObjectUtil.equals(sourceCurrency.getId(), targetCurrency.getId())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot be exchange");
//        }
//        var isUsdOfSource = ObjectUtil.equals(sourceCurrency.getId(), usd.getId());
//        if (balance.getUsdCcuBalance().compareTo(BigDecimal.ZERO) > 0) {
//            var someBalance = JinqStream.of(balance)
//                    .select(s -> new Tuple4<>(
//                            isUsdOfSource ? s.getUsdCcuBalance() : s.getJapanCcuBalance(),
//                            isUsdOfSource ? s.getUsdCurrencyBalance() : s.getJapanCurrencyBalance(),
//                            isUsdOfSource ? s.getJapanCcuBalance() : s.getUsdCcuBalance(),
//                            isUsdOfSource ? s.getJapanCurrencyBalance() : s.getUsdCurrencyBalance()
//                    ))
//                    .getOnlyValue();
//            var sourceCcu = sourceBalance.divide(someBalance.getTwo().add(sourceBalance), 6, RoundingMode.FLOOR).multiply(someBalance.getOne());
//            var targetBalance = sourceCcu.divide(someBalance.getThree().add(sourceCcu), 6, RoundingMode.FLOOR).multiply(someBalance.getFour());
//            balanceList.add(new CcuBalanceModel()
//                    .setId(this.uuidUtil.v4())
//                    .setUsdType(usd)
//                    .setUsdCurrencyBalance(isUsdOfSource ? sourceBalance : targetBalance.multiply(new BigDecimal(-1)))
//                    .setUsdCcuBalance(isUsdOfSource ? sourceCcu.multiply(new BigDecimal(-1)) : sourceCcu)
//                    .setJapanType(japan)
//                    .setJapanCurrencyBalance(isUsdOfSource ? targetBalance.multiply(new BigDecimal(-1)) : sourceBalance)
//                    .setJapanCcuBalance(isUsdOfSource ? sourceCcu : sourceCcu.multiply(new BigDecimal(-1))));
//            return targetBalance;
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot be exchange");
//        }
//    }
//
//    private CcuBalanceModel getBalance() {
//        var balance = JinqStream.from(balanceList)
//                .group(s -> new Pair<>(s.getUsdType().getId(), s.getJapanType().getId()),
//                        (s, t) -> {
//                            var list = t.toList();
//                            return new CcuBalanceModel()
//                                    .setId(this.uuidUtil.v4())
//                                    .setUsdType(usd)
//                                    .setUsdCurrencyBalance(JinqStream.from(list).sumBigDecimal(m -> m.getUsdCurrencyBalance()))
//                                    .setUsdCcuBalance(JinqStream.from(list).sumBigDecimal(m -> m.getUsdCcuBalance()))
//                                    .setJapanCurrencyBalance(JinqStream.from(list).sumBigDecimal(m -> m.getJapanCurrencyBalance()))
//                                    .setJapanCcuBalance(JinqStream.from(list).sumBigDecimal(m -> m.getJapanCcuBalance()));
//                        }
//                )
//                .select(s -> s.getTwo())
//                .getOnlyValue();
//        return balance;
//    }
//
//    @BeforeEach
//    public void beforeEach() {
//        usd = new CurrencyModel()
//                .setId(this.uuidUtil.v4())
//                .setName("美元");
//        japan = new CurrencyModel()
//                .setId(this.uuidUtil.v4())
//                .setName("日元");
//        ccu = new CcuModel()
//                .setId(this.uuidUtil.v4())
//                .setName("中央币-美元-日元")
//                .setChildList(List.of(usd, japan));
//        this.balanceList = new ArrayList<>();
//        balanceList.add(new CcuBalanceModel()
//                .setId(this.uuidUtil.v4())
//                .setUsdType(usd)
//                .setUsdCurrencyBalance(new BigDecimal(0))
//                .setUsdCcuBalance(new BigDecimal(0))
//                .setJapanType(japan)
//                .setJapanCurrencyBalance(new BigDecimal(0))
//                .setJapanCcuBalance(new BigDecimal(0))
//        );
//    }

}
