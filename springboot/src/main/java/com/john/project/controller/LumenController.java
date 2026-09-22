package com.john.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.LumenContextCoreModel;
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

    private final LumenContextCoreModel lumenContextCoreModel = new LumenContextCoreModel();

    @PostMapping("/lumen/exchange")
    public ResponseEntity<?> exchange(@RequestParam String sourceCurrencyUnit, @RequestParam BigDecimal sourceCurrencyBalance) {
        var sourceCurrency = JinqStream.from(List.of(this.lumenContextCoreModel.getUsd(), this.lumenContextCoreModel.getJapan()))
                .where(s -> ObjectUtil.equals(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrencyBalance = this.lumenContextCoreModel.exchange(sourceCurrency, sourceCurrencyBalance);
        return ResponseEntity.ok(targetCurrencyBalance);
    }

    @PostMapping("/lumen/exchange/preview")
    @SneakyThrows
    public ResponseEntity<?> exchangePreview(@RequestParam String sourceCurrencyUnit, @RequestParam BigDecimal sourceCurrencyBalance) {
        var sourceCurrency = JinqStream.from(List.of(this.lumenContextCoreModel.getUsd(), this.lumenContextCoreModel.getJapan()))
                .where(s -> ObjectUtil.equals(s.getName(), sourceCurrencyUnit))
                .getOnlyValue();
        var targetCurrencyBalance = this.objectMapper.readValue(this.objectMapper.writeValueAsString(this.lumenContextCoreModel), LumenContextCoreModel.class).exchange(sourceCurrency, sourceCurrencyBalance);
        return ResponseEntity.ok(targetCurrencyBalance);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.lumenContextCoreModel.injectPair(new BigDecimal(1000 * 1000), new BigDecimal(1000 * 1000));
    }

}
