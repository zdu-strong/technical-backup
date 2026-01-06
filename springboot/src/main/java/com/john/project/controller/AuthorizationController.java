package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.UserModel;
import lombok.SneakyThrows;

@RestController
public class AuthorizationController extends BaseController {

    @PostMapping("/sign-in")
    @SneakyThrows
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password) {
        this.userService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);
        var accessToken = this.tokenService.generateAccessToken(userId, this.tokenService.getEncryptedPassword(password));
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-in/rsa/one-time")
    @SneakyThrows
    public ResponseEntity<?> signInByRsaOneTime(@RequestParam String username, @RequestParam String password) {
        this.userService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);
        var accessToken = this.tokenService.generateAccessToken(userId, password);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut() {
        if (this.permissionUtil.isSignIn(request)) {
            var id = this.tokenService.getDecodedJWTOfAccessToken(request).getId();
            if (this.tokenService.hasExistTokenEntity(id)) {
                this.tokenService.deleteTokenEntity(id);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up/rsa/one-time")
    public ResponseEntity<?> signUpByRsaOneTime(@RequestBody UserModel userModel) {
        this.validationFieldUtil.checkNotBlankOfNickname(userModel.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(userModel.getUsername());
        this.validationFieldUtil.checkNotBlankOfPassword(userModel.getPassword());
        this.userService.checkValidEmail(userModel);
        this.userService.checkUserRoleRelationListMustBeEmpty(userModel);

        var user = this.userService.create(userModel);
        user.setAccessToken(this.tokenService.generateAccessToken(user.getId()));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel) {
        this.validationFieldUtil.checkNotBlankOfNickname(userModel.getUsername());
        this.validationFieldUtil.checkNotEdgesSpaceOfUsername(userModel.getUsername());
        this.validationFieldUtil.checkNotBlankOfPassword(userModel.getPassword());
        this.userService.checkValidEmail(userModel);
        this.userService.checkUserRoleRelationListMustBeEmpty(userModel);

        userModel.setPassword(this.tokenService.getEncryptedPassword(userModel.getPassword()));
        var user = this.userService.create(userModel);
        user.setAccessToken(this.tokenService.generateAccessToken(user.getId()));
        return ResponseEntity.ok(user);
    }

}
