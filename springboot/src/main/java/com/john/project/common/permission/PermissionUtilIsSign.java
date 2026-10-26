package com.john.project.common.permission;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public abstract class PermissionUtilIsSign extends PermissionUtilGetOrganizeIdList {

    public void checkIsSignIn(HttpServletRequest request) {
        if (!this.isSignIn(request)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
    }

    public boolean isSignIn(HttpServletRequest request) {
        try {
            var accessToken = this.getAccessToken(request);
            return this.tokenService.getIsValidOfAccessToken(accessToken);
        } catch (Throwable e) {
            return false;
        }
    }

}
