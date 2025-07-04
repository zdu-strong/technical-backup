package com.john.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignOutTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        this.signOut();
        var url = new URIBuilder("/user/me").build();
        var response = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Please login first and then visit", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.createAccount(email);
    }

}
