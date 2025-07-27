package com.john.project.test.common.wasmUtil;

import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.Parser;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WasmUtilHandleStringTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var module = Parser.parse(new ClassPathResource("wasm/greet.wasm").getInputStream());
        var instance = Instance.builder(module).build();
        var inputMemory = Arrays.stream(instance.export("allocate").apply()).findFirst().getAsLong();
        instance.memory().writeString((int) inputMemory, "World", StandardCharsets.UTF_8);
        var outputMemory = Arrays.stream(instance.export("greet").apply(inputMemory)).findFirst().getAsLong();
        var result = instance.memory().readCString((int) outputMemory, StandardCharsets.UTF_8);
        assertEquals("Hello, World!", result);
    }

}
