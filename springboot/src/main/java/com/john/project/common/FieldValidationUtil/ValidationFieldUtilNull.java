package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilNull extends ValidationFieldUtilBase {

    @Transactional(readOnly = true)
    public void checkNullOfId(String id) {
        if (ObjectUtil.isNotNull(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must be null");
        }
    }

}
