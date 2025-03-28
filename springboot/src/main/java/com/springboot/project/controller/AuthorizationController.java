package com.springboot.project.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.UserModel;

import lombok.SneakyThrows;

@RestController
public class AuthorizationController extends BaseController {

    /**
     * username: email, userId
     * 
     * @param username
     * @param password
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws JsonProcessingException
     */
    @PostMapping("/sign-in")
    @SneakyThrows
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password) {
        this.userService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);
        var accessToken = this.tokenService.generateAccessToken(userId,
                this.encryptDecryptService.encryptByPublicKeyOfRSA(password));
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-in/one-time-password")
    @SneakyThrows
    public ResponseEntity<?> signInOneTime(@RequestParam String username, @RequestParam String password) {
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
            var id = this.tokenService.getDecodedJWTOfAccessToken(this.tokenService.getAccessToken(request)).getId();
            if (this.tokenService.hasExistTokenEntity(id)) {
                this.tokenService.deleteTokenEntity(id);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel) {
        this.userService.checkCannotEmptyOfUsername(userModel);
        this.userService.checkValidEmailForSignUp(userModel);
        this.userService.checkUserRoleRelationListMustBeEmpty(userModel);
        this.userService.checkCannotEmptyOfPassword(userModel);

        var user = this.userService.create(userModel);
        var accessToken = this.tokenService.generateAccessToken(user.getId());
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

}
