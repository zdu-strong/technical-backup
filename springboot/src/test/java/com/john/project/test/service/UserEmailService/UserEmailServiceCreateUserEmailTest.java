package com.john.project.test.service.UserEmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserEmailServiceCreateUserEmailTest extends BaseTest {
    private UserModel user;
    private String email;

    @Test
    public void test() {
        this.userEmailService.createUserEmail(this.email, this.user.getId());
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
