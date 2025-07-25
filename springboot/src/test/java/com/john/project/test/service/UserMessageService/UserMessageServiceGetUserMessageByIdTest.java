package com.john.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetUserMessageByIdTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() {
        var result = this.userMessageService.getUserMessageById(this.userMessage.getId(),
                this.userMessage.getUser().getId());
        assertEquals(this.userMessage.getId(), result.getId());
        assertEquals("Hello, World!", result.getContent());
        assertNotNull(result.getCreateDate());
        assertEquals(1, result.getPageNum());
        assertNotNull(result.getUpdateDate());
        assertNull(result.getUrl());
        assertTrue(StringUtils.isNotBlank(result.getUser().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this
                .createAccount(uuidUtil.v4() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setContent("Hello, World!");
        var message = this.userMessageService.sendMessage(userMessage, request);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
        this.userMessage = message;
    }
}
