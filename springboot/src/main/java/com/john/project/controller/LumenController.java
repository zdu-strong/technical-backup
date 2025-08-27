package com.john.project.controller;

import com.john.project.common.baseController.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;

@RestController
public class LumenController extends BaseController {

    @PostMapping("/lumen/exchange")
    public ResponseEntity<?> exchange(String sourceCurrencyUnit, BigDecimal sourceCurrencyBalance) {
        return ResponseEntity.ok(HELLO_WORLD);
    }

}
