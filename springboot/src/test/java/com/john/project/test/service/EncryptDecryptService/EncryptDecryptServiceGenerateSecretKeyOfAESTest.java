package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateSecretKeyOfAESTest extends BaseTest {

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES();
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByAES(
                        this.encryptDecryptService.encryptByAES(HELLO_WORLD, secretKeyOfAES),
                        secretKeyOfAES));
    }

}
