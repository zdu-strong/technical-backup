package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.UserModel;

@RestController
public class UserController extends BaseController {

    @GetMapping("/user/me")
    public ResponseEntity<?> getUserInfo() {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.userService.checkExistUserById(id);

        var userModel = this.userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> create(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotBlankOfPassword(user.getPassword());
        this.userService.checkRoleRelation(user, request);
        this.userService.checkValidEmailForSignUp(user);

        var userOne = this.userService.create(user);
        return ResponseEntity.ok(userOne);
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> update(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.userService.checkExistUserById(user.getId());
        this.userService.checkRoleRelation(user, request);
        this.userService.checkValidEmailForSignUp(user);

        this.userService.update(user);

        return ResponseEntity.ok().build();
    }

}
