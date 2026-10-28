package com.john.project.test.common.FieldValidationUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class ValidationFieldUtilCheckNotUsedOfEmailTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        this.validationFieldUtil.checkNotUsedOfEmail(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
    }

}
