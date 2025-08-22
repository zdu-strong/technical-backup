package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceDecryptByByPrivateKeyOfRSATest extends BaseTest {
    private String textOfEncryptByPublicKeyOfRSA;

    @Test
    public void test() {
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByByPrivateKeyOfRSA(this.textOfEncryptByPublicKeyOfRSA));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptByPublicKeyOfRSA = this.encryptDecryptService.encryptByPublicKeyOfRSA(HELLO_WORLD);
    }

}
