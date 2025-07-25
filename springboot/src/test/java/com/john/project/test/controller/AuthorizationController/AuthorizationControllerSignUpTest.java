package com.john.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignUpTest extends BaseTest {

    private String email;

    @Test
    public void test() {
        var result = this.createAccount(this.email);
        assertTrue(StringUtils.isNotBlank(result.getId()));
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
