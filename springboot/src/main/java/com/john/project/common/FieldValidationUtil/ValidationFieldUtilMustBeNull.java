package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public abstract class ValidationFieldUtilMustBeNull extends ValidationFieldUtilValidUrl {

    public void checkMustBeNullOfId(String id) {
        if (ObjectUtil.isNotNull(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must be null");
        }
    }

}
