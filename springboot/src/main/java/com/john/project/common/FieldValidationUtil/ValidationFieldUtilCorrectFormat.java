package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.lang.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilCorrectFormat extends ValidationFieldUtilNotExists {

    @Transactional(readOnly = true)
    public void checkCorrectFormatOfEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return;
        }
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }

}
