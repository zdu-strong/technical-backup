package com.john.project.websocket;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.uuid.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.hibernate.exception.GenericJDBCException;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserMessageWebSocketReceiveModel;
import com.john.project.model.UserMessageWebSocketSendModel;
import com.john.project.service.UserMessageService;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static eu.ciechanowiec.sneakyfun.SneakyPredicate.sneaky;

/**
 * Required parameters: String accessToken;
 * 
 * @author John Williams
 *
 */
@ServerEndpoint("/web-socket/user-message")
@Component
@Slf4j
public class UserMessageWebSocket {

    /**
     * Public accessible properties
     */
    @Getter
    private final static CopyOnWriteArrayList<UserMessageWebSocket> staticWebSocketList = new CopyOnWriteArrayList<UserMessageWebSocket>();

    private final ConcurrentHashMap<Long, UserMessageModel> onlineMessageMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, UserMessageModel> onlineMessageReceiveDateMap = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;
    private PermissionUtil permissionUtil;
    private UUIDUtil uuidUtil;
    private UserMessageService userMessageService;
    private HttpServletRequest request;
    private UserMessageWebSocketSendModel lastMessageCache = new UserMessageWebSocketSendModel().setTotalPages(0L)
            .setItems(Lists.newArrayList());
    private boolean ready = false;
    private PublishProcessor<String> checkIsSignInPublishProcessor;
    private Session webSocketSession;

    @OnOpen
    public void onOpen(Session session) {
        this.objectMapper = SpringUtil.getBean(ObjectMapper.class);
        this.permissionUtil = SpringUtil.getBean(PermissionUtil.class);
        this.uuidUtil = SpringUtil.getBean(UUIDUtil.class);
        this.userMessageService = SpringUtil.getBean(UserMessageService.class);
        this.webSocketSession = session;
        this.request = this.getRequest(session);
        this.permissionUtil.checkIsSignIn(request);
        staticWebSocketList.add(this);
    }

    /**
     * @param userMessageWebSocketReceiveModelString UserMessageWebSocketReceiveModel
     */
    @OnMessage
    public void OnMessage(String userMessageWebSocketReceiveModelString) {
        Thread.startVirtualThread(() -> {
            try {
                var userMessageWebSocketReceiveModel = this.objectMapper.readValue(
                        userMessageWebSocketReceiveModelString,
                        UserMessageWebSocketReceiveModel.class);
                if (userMessageWebSocketReceiveModel.getIsCancel() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
                if (userMessageWebSocketReceiveModel.getPageNum() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
                if (userMessageWebSocketReceiveModel.getPageNum() < 1) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }

                var pageNum = userMessageWebSocketReceiveModel.getPageNum();
                var onlineMessageReceiveDateModel = new UserMessageModel()
                        .setId(uuidUtil.v4())
                        .setPageNum(userMessageWebSocketReceiveModel.getPageNum())
                        .setCreateDate(new Date());
                if (!userMessageWebSocketReceiveModel.getIsCancel()) {
                    synchronized (this) {
                        this.onlineMessageMap.putIfAbsent(pageNum, new UserMessageModel());
                        this.onlineMessageReceiveDateMap.put(pageNum, onlineMessageReceiveDateModel);
                    }
                } else {
                    ThreadUtil.sleep(1000);
                    synchronized (this) {
                        var oldInfo = this.onlineMessageReceiveDateMap.getOrDefault(pageNum, null);
                        if (oldInfo != null) {
                            var canRemove = JinqStream.from(List.of(oldInfo, onlineMessageReceiveDateModel))
                                    .sortedBy(m -> m.getId())
                                    .sortedBy(m -> m.getCreateDate())
                                    .findFirst()
                                    .get()
                                    .getId().equals(oldInfo.getId());
                            if (!canRemove) {
                                return;
                            }
                        }
                        this.onlineMessageMap.remove(pageNum);
                        this.onlineMessageReceiveDateMap.remove(pageNum);
                    }
                }
            } catch (Throwable e) {
                this.OnError(this.webSocketSession, e);
            }
        });
    }

    @OnError
    @SneakyThrows
    public void OnError(Session session, Throwable e) {
        if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage()
                .contains("An established connection was aborted by the software in your host machine")) {
            // do noting
        } else if (e instanceof IllegalStateException) {
            // do noting
        } else if (e instanceof GenericJDBCException) {
            // do noting
        } else if (e instanceof CannotCreateTransactionException) {
            // do noting
        } else if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage()
                .contains("java.nio.channels.ClosedChannelException")) {
            // do noting
        } else if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage()
                .contains("No operations allowed after connection closed")) {
            // do noting
        } else {
            log.error(e.getMessage(), e);
        }
        session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
    }

    @OnClose
    public void onClose() {
        staticWebSocketList.remove(this);
    }

    public void sendMessage() {
        try {
            checkIsSignIn();
            var lastUserMessageWebSocketSendModel = this.userMessageService
                    .getMessageListByLastMessage(this.getPageSizeForLastMessage(), request);
            if (this.hasNeedSendAllOnlineMessage(lastUserMessageWebSocketSendModel)) {
                this.sendMessageForAllOnlineMessage(lastUserMessageWebSocketSendModel);
            } else if (this.hasNeedSendLastMessage(lastUserMessageWebSocketSendModel)) {
                this.sendAndUpdateOnlineMessage(lastUserMessageWebSocketSendModel);
            } else {
                this.sendMessageForOnlyOneOnlineMessage();
            }
            this.ready = true;
        } catch (Throwable e) {
            this.OnError(webSocketSession, e);
        }
    }

    private long getPageSizeForLastMessage() {
        var pageSize = this.ready ? 1L : 20L;
        return pageSize;
    }

    @SneakyThrows
    private boolean hasNeedSendLastMessage(UserMessageWebSocketSendModel lastUserMessageWebSocketSendModel) {
        return lastUserMessageWebSocketSendModel.getItems().stream()
                .filter(s -> s.getPageNum() == (long) lastUserMessageWebSocketSendModel.getTotalPages())
                .anyMatch(s -> hasChange(s));
    }

    @SneakyThrows
    private boolean hasNeedSendAllOnlineMessage(UserMessageWebSocketSendModel lastUserMessageWebSocketSendModel) {
        if (!this.ready) {
            return true;
        }
        if (lastUserMessageWebSocketSendModel.getTotalPages() < this.lastMessageCache.getTotalPages()) {
            return true;
        }
        if (lastUserMessageWebSocketSendModel.getTotalPages() > this.lastMessageCache.getTotalPages() + 1) {
            return true;
        }
        return this.onlineMessageMap.entrySet()
                .stream()
                .filter(s -> StringUtils.isBlank(s.getValue().getId()))
                .map(s -> (long) s.getKey())
                .filter(s -> !this.lastMessageCache.getItems().stream()
                        .anyMatch(m -> m.getPageNum() == (long) s))
                .filter(s -> !lastUserMessageWebSocketSendModel.getItems().stream()
                        .anyMatch(m -> m.getPageNum() == (long) s))
                .findFirst()
                .isPresent();
    }

    private void sendMessageForAllOnlineMessage(UserMessageWebSocketSendModel userMessageWebSocketSendModel) {
        var pageNumListOne = Flowable.fromIterable(this.onlineMessageMap.keySet())
                .toList()
                .blockingGet();
        var pageNumListTwo = JinqStream.from(Flowable.range(
                Math.max(1,
                        Math.max(this.lastMessageCache.getTotalPages().intValue(),
                                (int) (userMessageWebSocketSendModel.getTotalPages() - 20))),
                Math.max(
                        (int) (userMessageWebSocketSendModel.getTotalPages() - this.lastMessageCache.getTotalPages()),
                        0))
                .map(s -> (long) s)
                .toList()
                .blockingGet())
                .sortedDescendingBy(s -> s)
                .limit(20)
                .toList();
        var userMessageListOne = JinqStream.from(List.of(
                pageNumListOne,
                pageNumListTwo))
                .selectAllList(s -> s)
                .where(s -> s > 0)
                .where(s -> s < userMessageWebSocketSendModel.getTotalPages())
                .where(s -> !userMessageWebSocketSendModel.getItems().stream().anyMatch(m -> s == (long) m.getPageNum()))
                .distinct()
                .selectAllList(s -> this.userMessageService
                        .getUserMessageByPagination(s, 1L, request)
                        .getItems())
                .toList();
        var userMessageList = JinqStream.from(List.of(
                userMessageWebSocketSendModel.getItems(),
                userMessageListOne))
                .selectAllList(s -> s)
                .toList();
        var userMessageWebSocketSendNewModel = new UserMessageWebSocketSendModel()
                .setTotalPages(userMessageWebSocketSendModel.getTotalPages())
                .setItems(userMessageList);
        this.sendAndUpdateOnlineMessage(userMessageWebSocketSendNewModel);
    }

    private void sendMessageForOnlyOneOnlineMessage() {
        var pageNum = getPageNumForOnlineMessage();
        if (ObjectUtil.isNull(pageNum)) {
            return;
        }

        var userMessageList = this.userMessageService.getUserMessageByPagination(pageNum, 1L, request)
                .getItems();
        this.sendAndUpdateOnlineMessage(new UserMessageWebSocketSendModel()
                .setItems(userMessageList)
                .setTotalPages(null));
    }

    private Long getPageNumForOnlineMessage() {
        if (RandomUtil.randomInt(100) < 80) {
            return null;
        }

        var pageNumList = this.onlineMessageMap.keySet().stream().toList();
        if (pageNumList.isEmpty()) {
            return null;
        }
        var pageNum = pageNumList.get(RandomUtil.randomInt(pageNumList.size()));
        return pageNum;
    }

    private MockHttpServletRequest getRequest(Session session) {
        var accessToken = JinqStream.from(new URIBuilder(session.getRequestURI())
                .getQueryParams())
                .where(s -> s.getName().equals("accessToken"))
                .select(s -> s.getValue())
                .findOne()
                .orElse(StringUtils.EMPTY);
        var request = new MockHttpServletRequest();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        request.addHeader(HttpHeaders.AUTHORIZATION, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
        return request;
    }

    @SneakyThrows
    private synchronized void sendAndUpdateOnlineMessage(UserMessageWebSocketSendModel userMessageWebSocketSendModel) {
        var userMessageWebSocketSendNewModel = new UserMessageWebSocketSendModel()
                .setTotalPages(userMessageWebSocketSendModel.getTotalPages())
                .setItems(userMessageWebSocketSendModel.getItems()
                        .stream()
                        .filter(s -> hasChange(s))
                        .toList());
        if (userMessageWebSocketSendNewModel.getTotalPages() == null
                && userMessageWebSocketSendNewModel.getItems().isEmpty()) {
            return;
        }
        this.webSocketSession.getBasicRemote()
                .sendText(this.objectMapper
                        .writeValueAsString(userMessageWebSocketSendNewModel));
        for (var userMessage : userMessageWebSocketSendNewModel.getItems()) {
            this.onlineMessageMap.replace(userMessage.getPageNum(), userMessage);
        }
        var lastMessage = userMessageWebSocketSendModel
                .getItems()
                .stream()
                .filter(s -> userMessageWebSocketSendModel.getTotalPages() != null)
                .filter(s -> s.getPageNum() == (long) userMessageWebSocketSendModel.getTotalPages())
                .findFirst()
                .orElse(null);
        if (lastMessage != null) {
            for (var userMessage : this.lastMessageCache.getItems()) {
                this.onlineMessageMap.replace(userMessage.getPageNum(), userMessage);
            }
            this.lastMessageCache = new UserMessageWebSocketSendModel()
                    .setTotalPages(lastMessage.getPageNum())
                    .setItems(List.of(lastMessage));
        }
    }

    @SneakyThrows
    private boolean hasChange(UserMessageModel userMessage) {
        var oldUserMessage = this.onlineMessageMap.getOrDefault(userMessage.getPageNum(),
                new UserMessageModel());
        var hasChange = !this.objectMapper.writeValueAsString(oldUserMessage)
                .equals(this.objectMapper.writeValueAsString(userMessage));
        if (hasChange) {
            if (this.lastMessageCache.getItems()
                    .stream()
                    .filter(s -> s.getPageNum() == (long) userMessage.getPageNum())
                    .anyMatch(sneaky(s -> this.objectMapper.writeValueAsString(s)
                            .equals(this.objectMapper.writeValueAsString(userMessage))))) {
                hasChange = false;
            }
        }
        return hasChange;
    }

    private void checkIsSignIn() {
        if (this.checkIsSignInPublishProcessor != null) {
            this.checkIsSignInPublishProcessor.onNext(StringUtils.EMPTY);
            return;
        }
        synchronized (this) {
            if (this.checkIsSignInPublishProcessor != null) {
                return;
            }
            PublishProcessor<String> publishProcessorOne = PublishProcessor.create();
            publishProcessorOne
                    .throttleLatest(1, TimeUnit.SECONDS, true)
                    .delay(1, TimeUnit.MILLISECONDS)
                    .doOnNext((s) -> {
                        try {
                            this.permissionUtil.checkIsSignIn(request);
                        } catch (Throwable e) {
                            this.OnError(webSocketSession, e);
                        }
                    })
                    .retry()
                    .subscribe();
            this.checkIsSignInPublishProcessor = publishProcessorOne;
        }
    }

}
