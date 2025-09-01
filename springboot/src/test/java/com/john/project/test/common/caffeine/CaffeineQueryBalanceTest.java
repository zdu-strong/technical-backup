package com.john.project.test.common.caffeine;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaffeineQueryBalanceTest extends BaseTest {

    private final long intervalMilliseconds = 10;
    private final AsyncLoadingCache<Long, String> caffeineLoadCache = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .buildAsync(this::queryBalanceFromDatabase);

    @Test
    @SneakyThrows
    public void test() {
        var timer = new TimeInterval();
        Flowable.range(1, 1000 * 10)
                .parallel(1000 * 10)
                .runOn(Schedulers.from(applicationTaskExecutor))
                .doOnNext((s) -> queryBalance())
                .sequential()
                .blockingSubscribe();
        var costTimes = timer.interval();
        assertTrue(costTimes < 500);
    }

    @SneakyThrows
    private String queryBalance() {
        return caffeineLoadCache.get((System.currentTimeMillis() + intervalMilliseconds) / intervalMilliseconds).get();
    }

    private String queryBalanceFromDatabase(long key) {
        var waitMilliseconds = Math.max(key * intervalMilliseconds - System.currentTimeMillis(), 0);
        if (waitMilliseconds > 0) {
            ThreadUtil.sleep(waitMilliseconds);
        }
        ThreadUtil.sleep(100);
        return HELLO_WORLD;
    }

}
