package com.john.project.controller;

import com.john.project.enums.SystemPermissionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.OrganizeModel;

import java.util.Optional;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize/create")
    public ResponseEntity<?> create(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNullOfId(organizeModel.getId());
        this.permissionUtil.checkAnyPermission(request,
                Optional.ofNullable(organizeModel.getParent())
                        .map(s -> s.getId())
                        .orElse(StringUtils.EMPTY),
                SystemPermissionEnum.SUPER_ADMIN,
                SystemPermissionEnum.ORGANIZE_OWNER);
        this.validationFieldUtil.checkNotBlankOfOrganizeType(organizeModel.getOrganizeType());
        this.validationFieldUtil.checkValidOfOrganizeType(organizeModel);
        this.validationFieldUtil.checkNotDeletedOfOrganizeByParentOrganize(organizeModel);

        var organize = this.organizeUtil.create(organizeModel);
        return ResponseEntity.ok(organize);
    }

    @PostMapping("/organize/update")
    public ResponseEntity<?> update(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(organizeModel.getId());
        this.permissionUtil.checkAnyPermission(request,
                organizeModel.getId(),
                SystemPermissionEnum.SUPER_ADMIN,
                SystemPermissionEnum.ORGANIZE_OWNER);
        this.validationFieldUtil.checkNotDeletedOfOrganize(organizeModel.getId());
//        this.validationFieldUtil.checkNotBlankOfOrganizeType(organizeModel.getOrganizeType());
//        this.validationFieldUtil.checkValidOfOrganizeType(organizeModel);

        this.organizeUtil.update(organizeModel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organize/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.permissionUtil.checkAnyPermission(request,
                id,
                SystemPermissionEnum.SUPER_ADMIN,
                SystemPermissionEnum.ORGANIZE_OWNER);
        this.validationFieldUtil.checkNotDeletedOfOrganize(id);

        this.organizeUtil.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organize/move")
    public ResponseEntity<?> move(@RequestParam String id, @RequestParam(required = false) String parentId) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.validationFieldUtil.checkNotDeletedOfOrganize(id);
        this.validationFieldUtil.checkNotDeletedOfOrganize(parentId);
        this.permissionUtil.checkAnyPermission(request,
                id,
                SystemPermissionEnum.SUPER_ADMIN,
                SystemPermissionEnum.ORGANIZE_OWNER);
        this.permissionUtil.checkAnyPermission(request,
                parentId,
                SystemPermissionEnum.SUPER_ADMIN,
                SystemPermissionEnum.ORGANIZE_OWNER);

        this.organizeUtil.move(id, parentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/organize")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.validationFieldUtil.checkExistsOfOrganize(id);

        var organizeModel = this.organizeService.getById(id);
        return ResponseEntity.ok(organizeModel);
    }

}
