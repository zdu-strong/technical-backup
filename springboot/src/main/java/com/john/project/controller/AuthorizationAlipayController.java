package com.john.project.controller;

import java.nio.charset.StandardCharsets;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import lombok.SneakyThrows;

@RestController
public class AuthorizationAlipayController extends BaseController {

    @GetMapping("/sign-in/alipay/generate-qr-code")
    @SneakyThrows
    public ResponseEntity<?> generateQrCode() {
        var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                .setParameter("app_id", "2021002177648626")
                .setParameter("scope", "auth_user")
                .setParameter("redirect_uri", "https://kame-sennin.com/abc")
                .setParameter("state", "init")
                .build();
        var imageUrl = QrCodeUtil.generateAsBase64(
                url.toString(),
                QrConfig.create()
                        .setErrorCorrection(ErrorCorrectionLevel.H)
                        .setCharset(StandardCharsets.UTF_8)
                        .setWidth(200)
                        .setHeight(200),
                MediaType.IMAGE_PNG.getSubtype()
        );
        return ResponseEntity.ok(imageUrl);
    }

}
