package com.john.project.test.common.FieldValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.john.project.test.common.BaseTest.BaseTest;

public class ValidationFieldUtilCheckExistsOfOrganizeNotExistsTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.validationFieldUtil.checkExistsOfOrganize(uuidUtil.v4());
        });
    }

}
