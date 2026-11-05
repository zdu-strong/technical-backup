package com.john.project.controller;

import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageRoleQueryController extends BaseController {

    @PostMapping("/manage/role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestBody SuperAdminRoleQueryPaginationModel superAdminRoleQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.ORGANIZE_OWNER);

        var paginationModel = this.roleService.searchRoleForManageByPagination(request, superAdminRoleQueryPaginationModel);

        return ResponseEntity.ok(paginationModel);
    }

}
