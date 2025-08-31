package com.john.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.LumenCcuBalanceModel;
import com.john.project.model.LumenContextModel;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class LumenController extends BaseController {

    private final LumenContextModel lumenContextModel = new LumenContextModel();

    @PostMapping("/lumen/exchange")
    public ResponseEntity<?> exchange(String sourceCurrencyUnit, BigDecimal sourceCurrencyBalance) {
        var sourceCurrency = JinqStream.from(List.of(this.lumenContextModel.getUsd(), this.lumenContextModel.getJapan()))
                .where(s -> ObjectUtil.equals(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrency = JinqStream.from(List.of(this.lumenContextModel.getUsd(), this.lumenContextModel.getJapan()))
                .where(s -> ObjectUtil.notEqual(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrencyBalance = this.lumenContextModel.exchange(sourceCurrency, sourceCurrencyBalance);
        var result = new LumenCcuBalanceModel()
                .setId(this.uuidUtil.v7())
                .setCurrency(targetCurrency)
                .setCurrencyBalance(targetCurrencyBalance);
        return ResponseEntity.ok(result);
    }

}
