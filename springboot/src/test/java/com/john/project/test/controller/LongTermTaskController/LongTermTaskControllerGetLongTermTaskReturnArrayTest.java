package com.john.project.test.controller.LongTermTaskController;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.common.collect.Lists;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskReturnArrayTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            var httpHeaders = new HttpHeaders();
            httpHeaders.addAll("MyCustomHeader", Lists.newArrayList(HELLO_WORLD));
            httpHeaders.set("MySecondCustomHeader", HELLO_WORLD);
            return ResponseEntity.ok().headers(httpHeaders).body(new String[] { HELLO_WORLD, "I love girl" });
        }), new ParameterizedTypeReference<String[]>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().length);
        assertEquals(HELLO_WORLD, result.getBody()[0]);
        assertEquals("I love girl", result.getBody()[1]);
        assertEquals(result.getHeaders().get("MyCustomHeader").size(), 1);
        assertEquals(result.getHeaders().get("MyCustomHeader").get(0), HELLO_WORLD);
        assertEquals(result.getHeaders().get("MySecondCustomHeader").get(0), HELLO_WORLD);
    }

}
