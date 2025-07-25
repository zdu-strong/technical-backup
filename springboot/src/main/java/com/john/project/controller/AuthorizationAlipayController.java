package com.john.project.controller;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.john.project.common.baseController.BaseController;
import lombok.SneakyThrows;

@RestController
public class AuthorizationAlipayController extends BaseController {

    @GetMapping("/sign-in/alipay/generate-qr-code")
    @SneakyThrows
    public ResponseEntity<?> generateQrCode() {
        var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                .setParameter("app_id", "2021002177648626").setParameter("scope", "auth_user")
                .setParameter("redirect_uri", "https://kame-sennin.com/abc").setParameter("state", "init").build();
        var configOfQR = Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H,
                EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8
        );
        var bitMatrix = new QRCodeWriter().encode(url.toString(), BarcodeFormat.QR_CODE, 200, 200, configOfQR);
        try (var output = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, MediaType.IMAGE_PNG.getSubtype(), output);
            var pngData = output.toByteArray();
            var imageData = Base64.getEncoder().encodeToString(pngData);
            var imageUrl = "data:image/png;base64," + imageData;
            return ResponseEntity.ok(imageUrl);
        }
    }

}
