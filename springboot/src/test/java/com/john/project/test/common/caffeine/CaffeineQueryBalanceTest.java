package com.john.project.test.common.caffeine;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaffeineQueryBalanceTest extends BaseTest {

    private final long intervalMilliseconds = 10;

    @Test
    @SneakyThrows
    public void test() {
        var caffeineLoadCache = Caffeine.newBuilder()
                .executor(applicationTaskExecutor)
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .buildAsync((String key) -> queryBalance());
        var timer = new TimeInterval();
        Flowable.range(1, 1000 * 10)
                .parallel(1000 * 10)
                .runOn(Schedulers.from(applicationTaskExecutor))
                .doOnNext((s) -> {
                    ThreadUtil.sleep(10);
                    caffeineLoadCache.get(String.valueOf(new Date().getTime() / intervalMilliseconds)).get();
                })
                .sequential()
                .blockingSubscribe();
        var costTimes = timer.interval();
        assertTrue(costTimes < 500);
    }

    private String queryBalance() {
        ThreadUtil.sleep(100);
        return HELLO_WORLD;
    }

}
