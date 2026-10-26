package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.UserMessageModel;

@RestController
public class UserMessageController extends BaseController {

    @PostMapping("/user-message/send")
    public ResponseEntity<?> sendMessage(@RequestBody UserMessageModel userMessageModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUserMessageContent(userMessageModel);
        this.validationFieldUtil.checkValidOfUrl(userMessageModel.getUrl());

        var userMessage = this.userMessageService.sendMessage(request, userMessageModel);
        return ResponseEntity.ok(userMessage);
    }

    @PostMapping("/user-message/recall")
    public ResponseEntity<?> recallMessage(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageService.checkExistsUserMessage(id);
        this.userMessageService.checkCanRecallUserMessage(request, id);

        this.userMessageService.recallMessage(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-message/delete")
    public ResponseEntity<?> deleteMessage(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageService.checkExistsUserMessage(id);
        this.userMessageService.checkCanDeleteUserMessage(request, id);

        this.userMessageService.deleteMessage(request, id);
        return ResponseEntity.ok().build();
    }

}
