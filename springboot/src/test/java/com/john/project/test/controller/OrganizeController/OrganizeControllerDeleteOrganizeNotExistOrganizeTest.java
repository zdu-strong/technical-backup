package com.john.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerDeleteOrganizeNotExistOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/organize/delete").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Throwable.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Organize does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.createAccount(email);
        this.organizeId = this.uuidUtil.v4();
    }
}
