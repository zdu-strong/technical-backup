package com.john.project.controller;

import com.john.project.model.SuperAdminOrganizeQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminOrganizeQueryController extends BaseController {

    @PostMapping("/super-admin/organize/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestBody SuperAdminOrganizeQueryPaginationModel superAdminOrganizeQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var paginationModel = this.organizeService.searchOrganizeForSuperAdminByPagination(superAdminOrganizeQueryPaginationModel);

        return ResponseEntity.ok(paginationModel);
    }

}
