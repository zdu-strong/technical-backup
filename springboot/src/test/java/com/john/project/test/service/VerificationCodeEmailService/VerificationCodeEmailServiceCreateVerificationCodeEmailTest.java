package com.john.project.test.service.VerificationCodeEmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.ReUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class VerificationCodeEmailServiceCreateVerificationCodeEmailTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        var result = this.verificationCodeEmailService.createVerificationCodeEmail(this.email);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertEquals(this.email, result.getEmail());
        assertTrue(StringUtils.isNotBlank(result.getVerificationCode()));
        assertEquals(6, result.getVerificationCode().length());
        assertTrue(ReUtil.isMatch("^[0-9]{6}$", result.getVerificationCode()));
        assertEquals(6, result.getVerificationCodeLength());
        assertFalse(result.getHasUsed());
        assertFalse(result.getIsPassed());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
