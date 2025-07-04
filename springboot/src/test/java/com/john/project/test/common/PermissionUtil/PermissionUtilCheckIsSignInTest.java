package com.john.project.test.common.PermissionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;


import com.john.project.test.common.BaseTest.BaseTest;

public class PermissionUtilCheckIsSignInTest extends BaseTest {
    @Test
    public void test() {
        this.permissionUtil.checkIsSignIn(this.request);
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "@gmail.com";
        var user = this.createAccount(email);
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
