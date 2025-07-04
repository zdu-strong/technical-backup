package com.john.project.test.service.UserEmailService;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserEmailServiceCreateUserEmailAddSameEamilTest extends BaseTest {
    private UserModel user;
    private String email;

    @Test
    public void test() {
        assertThrows(Throwable.class, () -> {
            this.userEmailService.createUserEmail(this.email, this.user.getId());
        });
        try {
            this.userEmailService.createUserEmail(this.email, this.user.getId());
        } catch (Throwable e) {
            assertTrue(List.of(DataIntegrityViolationException.class, JpaSystemException.class).contains(e.getClass()));
        }
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
    }

}
