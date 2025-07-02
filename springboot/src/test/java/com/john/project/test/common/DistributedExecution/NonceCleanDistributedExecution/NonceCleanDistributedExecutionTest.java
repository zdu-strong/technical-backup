package com.john.project.test.common.DistributedExecution.NonceCleanDistributedExecution;

import com.john.project.constant.DateFormatConstant;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NonceCleanDistributedExecutionTest extends BaseTest {

    @Test
    public void test() {
        this.distributedExecutionUtil.refreshData(nonceCleanDistributedExecution);
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        var nonce = UUID.randomUUID().toString();
        var timestamp = FastDateFormat.getInstance(DateFormatConstant.UTC).format(new Date());
        URI url = new URIBuilder("/").build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Nonce", nonce);
        httpHeaders.set("X-Timestamp", timestamp);
        ResponseEntity<String> response = this.testRestTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getBody().length());
        assertEquals("Hello, World!", response.getBody());
    }

}
