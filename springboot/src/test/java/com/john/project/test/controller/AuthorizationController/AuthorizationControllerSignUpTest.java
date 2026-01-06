package com.john.project.test.controller.AuthorizationController;

import com.google.common.collect.Lists;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizationControllerSignUpTest extends BaseTest {

    private String email;

    @Test
    @SneakyThrows
    public void test() {
        var verificationCodeEmail = sendVerificationCode(email);
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp
                .setUsername(email)
                .setPassword(this.email)
                .setUserEmailList(Lists.newArrayList(
                        new UserEmailModel()
                                .setEmail(email)
                                .setVerificationCodeEmail(verificationCodeEmail)));
        var url = new URIBuilder("/sign-up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(userModelOfSignUp),
                UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
