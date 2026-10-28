package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public abstract class ValidationFieldUtilEmpty extends ValidationFieldUtilNull {

    @Transactional(readOnly = true)
    public void checkEmptyOfRoleList(UserModel user) {
        if (ObjectUtil.isEmpty(user.getRoleList())) {
            user.setRoleList(List.of());
        }
        if (!ObjectUtil.isEmpty(user.getRoleList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList must be empty");
        }
    }

}
