package com.john.project.test.common.caffeine;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaffeineTest extends BaseTest {

    private final AsyncLoadingCache<String, String> caffeineLoadCache = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .refreshAfterWrite(29, TimeUnit.SECONDS)
            .buildAsync(this::query);

    @Test
    @SneakyThrows
    public void test() {
        var timer = new TimeInterval();
        for (var i = 1000 * 1000; i > 0; i--) {
            caffeineLoadCache.get(StringUtils.EMPTY).get();
        }
        var costTimes = timer.interval();
        assertTrue(costTimes < 1000);
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        caffeineLoadCache.get(StringUtils.EMPTY).get();
    }

    private String query(String key) {
        ThreadUtil.sleep(100);
        return HELLO_WORLD;
    }

}
