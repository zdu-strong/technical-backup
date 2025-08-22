package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceDecryptByAESTest extends BaseTest {

    private String textOfEncryptOfAES;

    @Test
    public void test() {
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByAES(this.textOfEncryptOfAES));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptOfAES = this.encryptDecryptService.encryptByAES(HELLO_WORLD);
    }

}
