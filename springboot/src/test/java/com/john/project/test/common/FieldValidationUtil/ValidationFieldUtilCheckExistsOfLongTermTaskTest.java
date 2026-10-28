package com.john.project.test.common.FieldValidationUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class ValidationFieldUtilCheckExistsOfLongTermTaskTest extends BaseTest {

    private String longTermTaskId;

    @Test
    public void test() {
        this.validationFieldUtil.checkExistsOfLongTermTask(this.longTermTaskId);
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
    }

}
