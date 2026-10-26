package com.john.project.common.permission;

import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public abstract class PermissionUtilGetUserId extends PermissionUtilBase {

    public String getUserId(HttpServletRequest request) {
        var accessToken = this.getAccessToken(request);
        var userId = JWT.decode(accessToken).getSubject();
        return userId;
    }

    public String getIdOfAccessToken(HttpServletRequest request) {
        var accessToken = this.getAccessToken(request);
        var idOfAccessToken = JWT.decode(accessToken).getId();
        return idOfAccessToken;
    }

}
