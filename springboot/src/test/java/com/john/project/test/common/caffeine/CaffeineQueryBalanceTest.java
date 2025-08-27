package com.john.project.test.common.caffeine;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.constant.DateFormatConstant;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaffeineQueryBalanceTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var caffeineLoadCache = Caffeine.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .buildAsync((String key) -> {
                    return queryBalance(key);
                });
        var timer = new TimeInterval();
        Flowable.range(0, 1000 * 10)
                .parallel(1000 * 10)
                .runOn(Schedulers.from(Executors.newVirtualThreadPerTaskExecutor()))
                .doOnNext((s) -> {
                    ThreadUtil.sleep(10);
                    caffeineLoadCache.get(FastDateFormat.getInstance(StrFormatter.format("{}.SS", DateFormatConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)).format(new Date())).get();
                })
                .sequential()
                .blockingSubscribe();
        var costTimes = timer.interval();
        assertTrue(costTimes < 500);
    }

    private String queryBalance(String queryDateString) {
        ThreadUtil.sleep(100);
        return HELLO_WORLD;
    }

}
