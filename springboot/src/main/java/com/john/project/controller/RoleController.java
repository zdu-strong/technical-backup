package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/role/update")
    public ResponseEntity<?> update(@RequestBody RoleModel roleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfRoleName(roleModel.getName());
        this.validationFieldUtil.checkNotEmptyOfPermissionList(roleModel);
        this.roleService.checkExistRoleById(roleModel.getId());
        this.roleService.checkCanUpdateRole(roleModel, request);

        this.roleService.update(roleModel);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.roleService.checkExistRoleById(id);

        this.roleService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/role")
    public ResponseEntity<?> getRoleById(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.roleService.checkExistRoleById(id);

        var roleModel = this.roleService.getRoleById(id);
        return ResponseEntity.ok(roleModel);
    }

}
