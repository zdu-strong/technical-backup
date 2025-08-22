package com.john.project.test.common.wasmUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WasmUtilHandleStringTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var inputParams = Arrays.stream(ArrayUtils.toObject(objectMapper.writeValueAsBytes(HELLO_WORLD))).mapToLong(s -> s).toArray();
        var outputResult = this.objectMapper.readValue(ArrayUtils.toPrimitive(Arrays.stream(ArrayUtils.toObject(inputParams)).map(s -> Byte.valueOf(String.valueOf(s))).toList().toArray(new Byte[]{})), String.class);
        assertEquals(HELLO_WORLD, outputResult);
    }

}
