package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.test.common.BaseTest.BaseTest;
import tools.jackson.databind.JsonNode;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageThrowResponseStatusExceptionTest extends BaseTest {
    private String longTermTaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermTaskId,
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request"));
        var response = this.longTermTaskService.getLongTermTask(this.longTermTaskId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var result = (JsonNode) response.getBody();
        assertEquals("Bad Request", result.get("message").asText());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
    }

}
