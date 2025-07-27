package com.john.project.test.common.wasmUtil;

import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.Parser;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WasmUtilTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var module = Parser.parse(new ClassPathResource("wasm/simple.wasm").getInputStream());
        var instance = Instance.builder(module).build();
        var result = Arrays.stream(instance.export("sum").apply(5, 37)).findFirst().getAsLong();
        assertEquals(42, result);
    }

}
