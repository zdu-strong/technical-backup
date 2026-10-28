package com.john.project.common.FieldValidationUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilNotEdgesSpace extends ValidationFieldUtilNotBlank {

    @Transactional(readOnly = true)
    public void checkNotEdgesSpaceOfUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        if (username.trim().length() != username.length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }

}
