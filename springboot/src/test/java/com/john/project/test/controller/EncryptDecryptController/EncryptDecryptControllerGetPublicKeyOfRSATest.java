package com.john.project.test.controller.EncryptDecryptController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptControllerGetPublicKeyOfRSATest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/encrypt-decrypt/rsa/public-key").build();
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody()));
    }

}
