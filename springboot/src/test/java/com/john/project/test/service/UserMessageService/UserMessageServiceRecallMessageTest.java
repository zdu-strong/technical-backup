package com.john.project.test.service.UserMessageService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceRecallMessageTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() {
        this.userMessageService.recallMessage(this.userMessage.getId());
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(uuidUtil.v4() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setContent(HELLO_WORLD);
        var message = this.userMessageService.sendMessage(userMessage, request);
        assertEquals(36, message.getId().length());
        assertEquals(HELLO_WORLD, message.getContent());
        assertEquals(userId, message.getUser().getId());
        this.userMessage = message;
    }
}
