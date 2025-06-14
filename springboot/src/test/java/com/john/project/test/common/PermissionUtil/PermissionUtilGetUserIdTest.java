package com.john.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class PermissionUtilGetUserIdTest extends BaseTest {
    private UserModel user;

    @Test
    public void test() {
        var userId = this.permissionUtil.getUserId(this.request);
        assertTrue(StringUtils.isNotBlank(userId));
        assertEquals(this.user.getId(), userId);
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "@gmail.com";
        this.user = this.createAccount(email);
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
