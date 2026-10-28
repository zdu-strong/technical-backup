package com.john.project.common.FieldValidationUtil;

import com.john.project.model.UserMessageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilNotBlank extends ValidationFieldUtilNotEmpty {

    @Transactional(readOnly = true)
    public void checkNotBlankOfUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfNickname(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfUserMessageContent(UserMessageModel userMessageModel) {
        if (StringUtils.isBlank(userMessageModel.getUrl()) && StringUtils.isBlank(userMessageModel.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfRoleName(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleName cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfPermission(String permission) {
        if (StringUtils.isBlank(permission)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permission cannot be blank");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotBlankOfOrganizeType(String organizeType) {
        if (StringUtils.isBlank(organizeType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "organizeType cannot be blank");
        }
    }

}
