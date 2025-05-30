package com.john.project.test.service.UserEmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class UserEmailServiceCheckEmailIsNotUsedTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        this.userEmailService.checkIsNotUsedOfEmail(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
