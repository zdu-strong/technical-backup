package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.RoleModel;

@RestController
public class RoleController extends BaseController {

    @PostMapping("/role/create")
    public ResponseEntity<?> create(@RequestBody RoleModel roleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfRoleName(roleModel.getName());
        this.validationFieldUtil.checkNotEmptyOfPermissionList(roleModel);
        this.roleService.checkCanCreateRole(roleModel, request);

        var roleOneModel = this.roleService.create(roleModel);

        return ResponseEntity.ok(roleOneModel);
    }

}
