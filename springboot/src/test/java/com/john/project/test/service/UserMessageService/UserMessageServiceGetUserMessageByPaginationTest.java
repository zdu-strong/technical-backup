package com.john.project.test.service.UserMessageService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetUserMessageByPaginationTest extends BaseTest {

    @Test
    public void test() {
        var userMessagePagination = this.userMessageService.getUserMessageByPagination(1L, 1L, request);
        var message = JinqStream.from(userMessagePagination.getItems()).getOnlyValue();
        assertEquals(1, userMessagePagination.getItems().size());
        assertTrue(StringUtils.isNotBlank(message.getId()));
        assertNotNull(message.getCreateDate());
        assertEquals(1, message.getPageNum());
        assertNotNull(message.getUpdateDate());
        assertNull(message.getUrl());
        assertTrue(StringUtils.isNotBlank(message.getUser().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this
                .createAccount(uuidUtil.v4() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setContent(HELLO_WORLD);
        var message = this.userMessageService.sendMessage(userMessage, request);
        assertEquals(36, message.getId().length());
        assertEquals(HELLO_WORLD, message.getContent());
        assertEquals(userId, message.getUser().getId());
    }
}
