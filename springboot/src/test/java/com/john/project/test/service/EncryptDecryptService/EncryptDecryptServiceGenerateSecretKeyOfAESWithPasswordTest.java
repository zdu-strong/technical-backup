package com.john.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateSecretKeyOfAESWithPasswordTest extends BaseTest {

    private String text = "Hello, World!";
    private String password;

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(password);
        assertEquals(text,
                this.encryptDecryptService.decryptByAES(
                        this.encryptDecryptService.encryptByAES(text, secretKeyOfAES),
                        secretKeyOfAES));
        assertEquals(this.encryptDecryptService.generateSecretKeyOfAES(password),
                this.encryptDecryptService.generateSecretKeyOfAES(password));
    }

    @BeforeEach
    public void beforeEach() {
        this.password = this.uuidUtil.v4();
    }

}
