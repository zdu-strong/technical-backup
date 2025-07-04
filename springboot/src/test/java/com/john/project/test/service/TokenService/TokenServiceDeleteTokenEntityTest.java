package com.john.project.test.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.auth0.jwt.JWT;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class TokenServiceDeleteTokenEntityTest extends BaseTest {
    private UserModel user;
    private String jwtId;

    @Test
    public void test() {
        this.tokenService.deleteTokenEntity(jwtId);
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
        this.jwtId = JWT.decode(this.user.getAccessToken()).getId();
    }

}
