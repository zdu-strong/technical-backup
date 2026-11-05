package com.john.project.controller;

import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.SuperAdminOrganizeQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageOrganizeQueryController extends BaseController {

    @PostMapping("/manage/organize/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestBody SuperAdminOrganizeQueryPaginationModel superAdminOrganizeQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.ORGANIZE_OWNER);

        var paginationModel = this.organizeService.searchOrganizeForManageByPagination(request, superAdminOrganizeQueryPaginationModel);

        return ResponseEntity.ok(paginationModel);
    }

}
