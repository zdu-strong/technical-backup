package com.john.project.test.common.config.NonceControllerAdviceConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.util.Date;

import com.john.project.constant.DateFormatConstant;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.john.project.constant.NonceConstant;
import com.john.project.test.common.BaseTest.BaseTest;

public class NonceControllerAdviceConfigExpiredTimestampTest extends BaseTest {

    private String nonce;
    private String timestamp;

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/").build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Nonce", nonce);
        httpHeaders.set("X-Timestamp", timestamp);
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Nonce has expired", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        this.nonce = this.uuidUtil.v4();
        this.timestamp = FastDateFormat.getInstance(DateFormatConstant.UTC).format(
                DateUtils.addMilliseconds(new Date(), -(int) NonceConstant.NONCE_SURVIVAL_DURATION.toMillis() - 1));
    }
}
