package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.RoleModel;

import java.util.List;

@RestController
public class RoleController extends BaseController {

    @PostMapping("/role/create")
    public ResponseEntity<?> create(@RequestBody RoleModel roleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkMustBeNullOfId(roleModel.getId());
        this.validationFieldUtil.checkNotBlankOfRoleName(roleModel.getName());
        this.validationFieldUtil.checkNotEmptyOfPermissionList(roleModel);
        this.roleService.checkRoleRelation(request, roleModel);

        var roleOneModel = this.roleService.create(roleModel);

        return ResponseEntity.ok(roleOneModel);
    }

    @PostMapping("/role/update")
    public ResponseEntity<?> update(@RequestBody RoleModel roleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(roleModel.getId());
        this.validationFieldUtil.checkNotBlankOfRoleName(roleModel.getName());
        this.validationFieldUtil.checkNotEmptyOfPermissionList(roleModel);
        this.roleService.checkExistRoleById(roleModel.getId());
        this.roleService.checkRoleRelation(request, roleModel);

        this.roleService.update(roleModel);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.roleService.checkExistRoleById(id);
        this.roleService.checkRoleRelation(request, this.roleService.getRoleById(id).setPermissionList(List.of()));

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
