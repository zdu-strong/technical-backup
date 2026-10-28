package com.john.project.common.FieldValidationUtil;

import com.john.project.model.RoleModel;
import com.john.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class ValidationFieldUtilCanCreate extends ValidationFieldUtilNotDeleted {

    @Transactional(readOnly = true)
    public void checkCreateOfRole(HttpServletRequest request, RoleModel roleModel) {
        this.checkPermissionRelation(request, roleModel);
    }

    @Transactional(readOnly = true)
    public void checkCreateOfUser(HttpServletRequest request, UserModel userModel) {
        this.checkPermissionRelation(request, userModel);
    }

}
