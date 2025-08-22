package com.john.project.test.service.UserMessageService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetMessageListByLastMessageTest extends BaseTest {

    @Test
    public void test() {
        var result = this.userMessageService.getMessageListByLastMessage(1L, request);
        assertEquals(1, result.getTotalPages());
        var message = JinqStream.from(result.getItems()).getOnlyValue();
        assertTrue(StringUtils.isNotBlank(message.getId()));
        assertTrue(StringUtils.isNotBlank(message.getContent()));
        assertNotNull(message.getCreateDate());
        assertTrue(message.getPageNum() >= 1);
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
