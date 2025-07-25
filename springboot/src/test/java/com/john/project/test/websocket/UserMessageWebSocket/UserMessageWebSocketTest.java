package com.john.project.test.websocket.UserMessageWebSocket;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserMessageWebSocketReceiveModel;
import com.john.project.model.UserMessageWebSocketSendModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import static eu.ciechanowiec.sneakyfun.SneakyFunction.sneaky;

public class UserMessageWebSocketTest extends BaseTest {

    private UserModel user;
    private ReplayProcessor<UserMessageWebSocketReceiveModel> webSocketSendProcessor = ReplayProcessor.create(100);

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder(this.serverAddressProperties.getWebSocketServerAddress())
                .setPath("/web-socket/user-message")
                .setParameter("accessToken", this.user.getAccessToken())
                .build();
        var userMessageResultList = new ArrayList<UserMessageWebSocketSendModel>();
        new StandardWebSocketClient().execute(url, (session) -> session
                .send(webSocketSendProcessor.map(s -> session.textMessage(this.objectMapper.writeValueAsString(s))))
                .and(session.receive().map(sneaky((s) -> {
                    userMessageResultList
                            .add(this.objectMapper.readValue(s.getPayloadAsText(),
                                    UserMessageWebSocketSendModel.class));
                    return session.close();
                }))))
                .block(Duration.ofMinutes(1));
        assertEquals(1, userMessageResultList.size());
        assertEquals(1, JinqStream.from(userMessageResultList).select(s -> s.getTotalPages()).getOnlyValue());
        var userMessage = JinqStream.from(userMessageResultList).selectAllList(s -> s.getItems()).getOnlyValue();
        assertEquals("Hello, World!", userMessage.getContent());
        assertNull(userMessage.getUrl());
        assertEquals(this.user.getId(), userMessage.getUser().getId());
        assertNotNull(userMessage.getCreateDate());
        assertNotNull(userMessage.getCreateDate());
        assertEquals(1, userMessage.getPageNum());
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "@gmail.com";
        this.user = this.createAccount(email);
        var userMessage = new UserMessageModel().setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage, request);
    }

}
