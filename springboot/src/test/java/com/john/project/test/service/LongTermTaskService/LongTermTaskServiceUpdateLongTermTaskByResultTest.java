package com.john.project.test.service.LongTermTaskService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.john.project.model.LongTermTaskModel;
import com.john.project.test.common.BaseTest.BaseTest;
import tools.jackson.core.type.TypeReference;

public class LongTermTaskServiceUpdateLongTermTaskByResultTest extends BaseTest {
    private String longTermTaskId;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskByResult(this.longTermTaskId, ResponseEntity.ok(HELLO_WORLD));
        var response = this.longTermTaskService.getLongTermTask(this.longTermTaskId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var result = this.objectMapper.readValue(this.objectMapper.writeValueAsString(response.getBody()), new TypeReference<LongTermTaskModel<String>>() {
        });
        assertEquals(HELLO_WORLD, result.getResult());
        assertTrue(result.getIsDone());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
    }

}
