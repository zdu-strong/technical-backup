package com.john.project.service;

import java.util.Date;

import com.john.project.entity.UserEntity;
import com.john.project.entity.UserMessageEntity;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.PaginationModel;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserMessageWebSocketSendModel;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserMessageService extends BaseService {

    @Autowired
    private UserMessageDeactivateService userMessageDeactivateService;

    public UserMessageModel sendMessage(UserMessageModel userMessageModel, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var userMessageEntity = new UserMessageEntity();
        userMessageEntity.setId(newId());
        userMessageEntity.setCreateDate(new Date());
        userMessageEntity.setUpdateDate(new Date());
        userMessageEntity.setContent(userMessageModel.getContent());
        userMessageEntity.setIsRecall(false);
        userMessageEntity.setUser(userEntity);
        userMessageEntity.setFolderName(StringUtils.EMPTY);
        userMessageEntity.setFileName(StringUtils.EMPTY);
        userMessageEntity.setFolderSize(0L);

        if (StringUtils.isNotBlank(userMessageModel.getUrl())) {
            var storageFileModel = this.storage.storageUrl(userMessageModel.getUrl());
            userMessageEntity.setFolderName(storageFileModel.getFolderName())
                    .setFolderSize(storageFileModel.getFolderSize())
                    .setFileName(storageFileModel.getFileName())
                    .setContent(StringUtils.EMPTY);
        }
        this.persist(userMessageEntity);

        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    public void recallMessage(String id) {
        var userMessageEntity = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        userMessageEntity.setIsRecall(true);
        userMessageEntity.setUpdateDate(new Date());
        this.merge(userMessageEntity);
    }

    public void deleteMessage(String id, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var userMessageId = id;
        this.userMessageDeactivateService.create(userMessageId, userId);
    }

    @Transactional(readOnly = true)
    public UserMessageModel getUserMessageById(String id, String userId) {
        var userMessageEntity = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    @Transactional(readOnly = true)
    public PaginationModel<UserMessageModel> getUserMessageByPagination(long pageNum, long pageSize,
            HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var stream = this.streamAll(UserMessageEntity.class)
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne())
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        var userMessagePagination = new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId));
        return userMessagePagination;
    }

    @Transactional(readOnly = true)
    public UserMessageWebSocketSendModel getMessageListByLastMessage(long pageSize, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var stream = this.streamAll(UserMessageEntity.class)
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        var pagination = new PaginationModel<>(1L, pageSize, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId));
        var userMessageWebSocketSendModel = new UserMessageWebSocketSendModel()
                .setTotalPages(pagination.getTotalPages())
                .setItems(pagination.getItems());
        return userMessageWebSocketSendModel;
    }

    @Transactional(readOnly = true)
    public void checkExistsUserMessage(String id) {
        var exists = this.streamAll(UserMessageEntity.class).where(s -> s.getId().equals(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User message do not exists");
        }
    }

    @Transactional(readOnly = true)
    public void checkCanRecallUserMessage(String id, HttpServletRequest request) {
        var userMessageEntity = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (!userMessageEntity.getUser().getId().equals(this.permissionUtil.getUserId(request))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recall your own messages");
        }
        if (userMessageEntity.getIsRecall()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot recall again");
        }
    }

    @Transactional(readOnly = true)
    public void checkCanDeleteUserMessage(String id, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var exists = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete again");
        }
    }
}
