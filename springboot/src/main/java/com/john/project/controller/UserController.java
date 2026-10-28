package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        this.validationFieldUtil.checkNotDeletedOfUser(id);

        var userModel = this.userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/user/create/ml-kem/one-time")
    public ResponseEntity<?> create(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNullOfId(user.getId());
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotBlankOfPassword(user.getPassword());
        this.validationFieldUtil.checkCreateOfUser(request, user);
        this.validationFieldUtil.checkValidOfUserByEmail(user);
        this.validationFieldUtil.checkNotExistsOfUserByUsername(user.getUsername());

        var userOne = this.userService.create(user);
        return ResponseEntity.ok(userOne);
    }

    @PostMapping("/user/update/ml-kem/one-time")
    public ResponseEntity<?> update(@RequestBody UserModel user) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUsername(user.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(user.getUsername());
        this.validationFieldUtil.checkExistsOfUserByUserId(user.getId());
        this.validationFieldUtil.checkUpdateOfUser(request, user);
        this.validationFieldUtil.checkValidOfUserByEmail(user);
        this.validationFieldUtil.checkNotExistsOfUserByUsername(user.getUsername(), user.getId());

        this.userService.update(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.validationFieldUtil.checkExistsOfUserByUserId(id);
        this.validationFieldUtil.checkCanDeleteOfUser(request, id);

        this.userService.delete(id);

        return ResponseEntity.ok().build();
    }

}
