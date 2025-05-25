package com.john.project.test.service.LoggerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import cn.hutool.core.text.StrFormatter;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.common.collect.Lists;
import com.john.project.model.LoggerModel;
import com.john.project.test.common.BaseTest.BaseTest;
import ch.qos.logback.classic.Level;

public class LoggerServiceCreateLoggerTest extends BaseTest {

    private LoggerModel loggerModel;

    @Test
    public void test() {
        var result = this.loggerService.create(loggerModel);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(result.getHasException());
        assertEquals("Hello, World!", result.getMessage());
        assertEquals("java.lang.RuntimeException", result.getExceptionClassName());
        assertEquals("Bug", result.getExceptionMessage());
        assertTrue(result.getExceptionStackTrace().size() > 70);
        assertEquals("com.john.project.controller.HelloWorldController", result.getLoggerName());
        assertEquals(this.gitProperties.getCommitId(), result.getGitCommitId());
        assertEquals(Date.from(this.gitProperties.getCommitTime()), result.getGitCommitDate());
        assertNotNull(result.getCreateDate());
        assertEquals("com.john.project.controller.HelloWorldController", result.getCallerClassName());
        assertEquals("helloWorld", result.getCallerMethodName());
        assertEquals(15, result.getCallerLineNumber());
    }

    @BeforeEach
    public void BeforeEach() {
        this.loggerModel = new LoggerModel().setLevel(Level.ERROR.levelStr).setMessage("Hello, World!")
                .setHasException(true)
                .setExceptionClassName("java.lang.RuntimeException")
                .setExceptionMessage("Bug")
                .setExceptionStackTrace(new ArrayList<>())
                .setLoggerName("com.john.project.controller.HelloWorldController")
                .setGitCommitId(this.gitProperties.getCommitId())
                .setGitCommitDate(Date.from(this.gitProperties.getCommitTime()))
                .setCallerClassName("com.john.project.controller.HelloWorldController")
                .setCallerMethodName("helloWorld")
                .setCallerLineNumber(15L);
        setExceptionStackTrace(loggerModel, new RuntimeException("Some thing is wrong!"));
    }

    private void setExceptionStackTrace(LoggerModel loggerModel, Throwable nextError) {
        while (nextError != null) {
            loggerModel.getExceptionStackTrace().add(
                    StrFormatter.format("{}{}: {}",
                            loggerModel.getExceptionStackTrace().isEmpty() ? StringUtils.EMPTY : "Caused by: ",
                            nextError.getClass().getName(), Optional.ofNullable(nextError.getMessage())
                                    .filter(StringUtils::isNotBlank)
                                    .orElse(StringUtils.EMPTY)));
            loggerModel.getExceptionStackTrace().addAll(JinqStream
                    .from(Lists.newArrayList(nextError.getStackTrace()))
                    .select(s -> "at " + s.toString()).toList());
            nextError = nextError.getCause();
        }
    }

}
