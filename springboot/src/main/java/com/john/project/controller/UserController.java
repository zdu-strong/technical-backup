package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.UserModel;

import java.util.List;

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
        this.userService.checkExistUserByIdWhetherDeleted(id);

        var userModel = this.userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/user/create/ml-kem/one-time")
    public ResponseEntity<?> create(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkMustBeNullOfId(user.getId());
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotBlankOfPassword(user.getPassword());
        this.userService.checkRoleRelation(request, user);
        this.userService.checkValidEmail(user);
        this.userService.checkExistUsername(user.getUsername());

        var userOne = this.userService.create(user);
        return ResponseEntity.ok(userOne);
    }

    @PostMapping("/user/update/ml-kem/one-time")
    public ResponseEntity<?> update(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.userService.checkExistUserById(user.getId());
        this.userService.checkRoleRelation(request, user);
        this.userService.checkValidEmail(user);
        this.userService.checkExistUsername(user.getUsername(), user.getId());

        this.userService.update(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.userService.checkExistUserById(id);
        this.userService.checkRoleRelation(request, this.userService.getUser(id).setRoleList(List.of()).setIdentityCardList(List.of()));

        this.userService.delete(id);

        return ResponseEntity.ok().build();
    }

}
