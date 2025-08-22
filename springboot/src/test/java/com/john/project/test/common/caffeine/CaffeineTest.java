package com.john.project.test.common.caffeine;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaffeineTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var caffeineLoadCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .refreshAfterWrite(29, TimeUnit.SECONDS)
                .build((key) -> {
                    return query();
                });
        caffeineLoadCache.get(StringUtils.EMPTY);
        var timer = new TimeInterval();
        for (var i = 1000 * 1000; i > 0; i--) {
            caffeineLoadCache.get(StringUtils.EMPTY);
        }
        var costTimes = timer.interval();
        assertTrue(costTimes < 200);
    }

    private String query() {
        ThreadUtil.sleep(100);
        return HELLO_WORLD;
    }

}
