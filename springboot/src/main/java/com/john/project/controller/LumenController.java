package com.john.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.LumenCcuBalanceModel;
import com.john.project.model.LumenContextModel;
import lombok.SneakyThrows;
import org.jinq.orm.stream.JinqStream;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class LumenController extends BaseController {

    private final LumenContextModel lumenContextModel = new LumenContextModel();

    @PostMapping("/lumen/exchange")
    public ResponseEntity<?> exchange(@RequestParam String sourceCurrencyUnit, @RequestParam BigDecimal sourceCurrencyBalance) {
        var sourceCurrency = JinqStream.from(List.of(this.lumenContextModel.getUsd(), this.lumenContextModel.getJapan()))
                .where(s -> ObjectUtil.equals(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrencyBalance = this.lumenContextModel.exchange(sourceCurrency, sourceCurrencyBalance);
        return ResponseEntity.ok(targetCurrencyBalance);
    }

    @PostMapping("/lumen/exchange/preview")
    @SneakyThrows
    public ResponseEntity<?> exchangePreview(@RequestParam String sourceCurrencyUnit, @RequestParam BigDecimal sourceCurrencyBalance) {
        var sourceCurrency = JinqStream.from(List.of(this.lumenContextModel.getUsd(), this.lumenContextModel.getJapan()))
                .where(s -> ObjectUtil.equals(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrencyBalance = this.objectMapper.readValue(this.objectMapper.writeValueAsString(this.lumenContextModel), LumenContextModel.class).exchange(sourceCurrency, sourceCurrencyBalance);
        return ResponseEntity.ok(targetCurrencyBalance);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.lumenContextModel.injectPair(new BigDecimal(1000 * 1000), new BigDecimal(1000 * 1000));
    }

}
