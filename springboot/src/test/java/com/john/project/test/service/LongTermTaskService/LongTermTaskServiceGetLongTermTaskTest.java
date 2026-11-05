package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.john.project.model.LongTermTaskModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceGetLongTermTaskTest extends BaseTest {

    private String longTermTaskId;

    @Test
    public void test() {
        var response = this.longTermTaskService.getLongTermTask(this.longTermTaskId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var result = (LongTermTaskModel<?>) response.getBody();
        assertNotNull(result.getId());
        assertFalse(result.getIsDone());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
    }

}
