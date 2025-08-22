package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceEncryptWithFixedSaltByAESTest extends BaseTest {

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptWithFixedSaltByAES(HELLO_WORLD);
        assertEquals(HELLO_WORLD, this.encryptDecryptService.decryptByAES(result));
        assertEquals(this.encryptDecryptService.encryptWithFixedSaltByAES(HELLO_WORLD),
                this.encryptDecryptService.encryptWithFixedSaltByAES(HELLO_WORLD));
    }

}
