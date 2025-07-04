package com.john.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskNotExistsTaskTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/long-term-task")
                .setParameter("encryptedId", this.encryptDecryptService
                        .encryptByAES(uuidUtil.v4()))
                .build();
        var result = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The specified task does not exist", result.getBody().getMessage());
    }

}
