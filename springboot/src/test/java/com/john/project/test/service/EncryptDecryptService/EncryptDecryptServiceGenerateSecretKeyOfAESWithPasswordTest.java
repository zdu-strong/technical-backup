package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateSecretKeyOfAESWithPasswordTest extends BaseTest {

    private String password;

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(password);
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByAES(
                        this.encryptDecryptService.encryptByAES(HELLO_WORLD, secretKeyOfAES),
                        secretKeyOfAES));
        assertEquals(this.encryptDecryptService.generateSecretKeyOfAES(password),
                this.encryptDecryptService.generateSecretKeyOfAES(password));
    }

    @BeforeEach
    public void beforeEach() {
        this.password = this.uuidUtil.v4();
    }

}
