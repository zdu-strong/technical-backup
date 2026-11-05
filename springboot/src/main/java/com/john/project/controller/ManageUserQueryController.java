package com.john.project.controller;

import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.SuperAdminUserQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageUserQueryController extends BaseController {

    @PostMapping("/manage/user/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestBody SuperAdminUserQueryPaginationModel superAdminUserQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.ORGANIZE_OWNER);

        var pagination = this.userService.searchForManageByPagination(request, superAdminUserQueryPaginationModel);
        return ResponseEntity.ok(pagination);
    }

}
