package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateKeyPairOfRSATest extends BaseTest {

    @Test
    public void test() {
        var keyPair = this.encryptDecryptService.generateKeyPairOfRSA();
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByByPrivateKeyOfRSA(
                        this.encryptDecryptService.encryptByPublicKeyOfRSA(HELLO_WORLD, keyPair.getPublicKeyOfRSA()),
                        keyPair.getPrivateKeyOfRSA()));
    }

}
