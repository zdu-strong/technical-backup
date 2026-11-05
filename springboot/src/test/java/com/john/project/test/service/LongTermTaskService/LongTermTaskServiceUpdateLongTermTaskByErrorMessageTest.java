package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageTest extends BaseTest {

    private String longTermTaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermTaskId,
                new RuntimeException("Internal Server Error"));
        var result = this.longTermTaskService.getLongTermTask(this.longTermTaskId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal Server Error", this.objectMapper.readTree(this.objectMapper.writeValueAsString(result.getBody())).get("message").asText());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
    }

}
