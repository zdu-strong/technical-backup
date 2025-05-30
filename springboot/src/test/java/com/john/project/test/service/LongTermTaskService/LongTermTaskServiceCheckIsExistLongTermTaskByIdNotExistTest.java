package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceCheckIsExistLongTermTaskByIdNotExistTest extends BaseTest {

    private String longTermtaskId;

    @Test
    public void test() {
        assertThrowsExactly(ResponseStatusException.class, () -> {
            this.longTermTaskService.checkHasExistById(this.longTermtaskId);
        });

    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.uuidUtil.v4();
    }

}
