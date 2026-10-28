package com.john.project.common.FieldValidationUtil;

import com.john.project.model.RoleModel;
import com.john.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class ValidationFieldUtilCanUpdate extends ValidationFieldUtilCanCreate {

    @Transactional(readOnly = true)
    public void checkUpdateOfRole(HttpServletRequest request, RoleModel roleModel) {
        this.checkPermissionRelation(request, roleModel);
    }

    @Transactional(readOnly = true)
    public void checkUpdateOfUser(HttpServletRequest request, UserModel userModel) {
        this.checkPermissionRelation(request, userModel);
    }

}
