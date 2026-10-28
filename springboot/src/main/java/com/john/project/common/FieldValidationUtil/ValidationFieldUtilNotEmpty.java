package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.RoleModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilNotEmpty extends ValidationFieldUtilEmpty {

    @Transactional(readOnly = true)
    public void checkNotEmptyOfPermissionList(RoleModel roleModel) {
        if (ObjectUtil.isEmpty(roleModel.getPermissionList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permission list cannot be empty");
        }
    }

}
