package com.john.project.test.DistributedExecution.NonceCleanDistributedExecution;

import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        Mockito.doCallRealMethod().when(this.distributedExecutionUtil)
                .refreshData(Mockito.any());
        var nonce = UUID.randomUUID().toString();
        var timestamp = FastDateFormat.getInstance(this.dateFormatProperties.getUTC()).format(new Date());
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
