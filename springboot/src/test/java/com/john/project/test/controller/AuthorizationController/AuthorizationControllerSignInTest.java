package com.john.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignInTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        var result = this.createAccount(this.email);
        assertNotNull(result);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getUsername()));
        assertTrue(StringUtils.isNotBlank(result.getAccessToken()));
        assertTrue(StringUtils.isBlank(result.getPassword()));
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(1, result.getUserEmailList().size());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getUserEmailList()).select(s -> s.getEmail()).getOnlyValue()));
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
