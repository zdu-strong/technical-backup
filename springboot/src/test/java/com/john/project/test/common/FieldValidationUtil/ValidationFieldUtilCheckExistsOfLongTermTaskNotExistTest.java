package com.john.project.test.common.FieldValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.john.project.test.common.BaseTest.BaseTest;

public class ValidationFieldUtilCheckExistsOfLongTermTaskNotExistTest extends BaseTest {

    private String longTermTaskId;

    @Test
    public void test() {
        assertThrowsExactly(ResponseStatusException.class, () -> {
            this.validationFieldUtil.checkExistsOfLongTermTask(this.longTermTaskId);
        });

    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.uuidUtil.v4();
    }

}
