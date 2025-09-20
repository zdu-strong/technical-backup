package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.RoleModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public abstract class ValidationFieldUtilNotEmpty extends ValidationFieldUtilNotBlank {

    public void checkNotEmptyOfPermissionList(RoleModel roleModel) {
        if (ObjectUtil.isEmpty(roleModel.getPermissionList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permissionList cannot be empty");
        }
    }

}
