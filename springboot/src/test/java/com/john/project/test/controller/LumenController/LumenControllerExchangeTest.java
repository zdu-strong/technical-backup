package com.john.project.test.controller.LumenController;

import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LumenControllerExchangeTest extends BaseTest {

    private OrganizeModel organizeModel;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/lumen/exchange")
                .setParameter("sourceCurrencyUnit", "USD")
                .setParameter("sourceCurrencyBalance", "1000")
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, BigDecimal.class);
        var result = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("997.008973"), result);
    }

}
