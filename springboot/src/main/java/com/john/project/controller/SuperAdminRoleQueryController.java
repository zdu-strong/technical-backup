package com.john.project.controller;

import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminRoleQueryController extends BaseController {

    @PostMapping("/super-admin/role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestBody SuperAdminRoleQueryPaginationModel superAdminRoleQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var paginationModel = this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel);

        return ResponseEntity.ok(paginationModel);
    }

}
