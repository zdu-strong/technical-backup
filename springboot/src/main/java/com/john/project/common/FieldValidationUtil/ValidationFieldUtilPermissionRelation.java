package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.entity.UserEntity;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.RoleModel;
import com.john.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public abstract class ValidationFieldUtilPermissionRelation extends ValidationFieldUtilValidUrl {

    protected void checkPermissionRelation(HttpServletRequest request, RoleModel roleModel) {
        var roleName = roleModel.getName();
        var roleId = roleModel.getId();

        List<PermissionRelationModel> oldPermissions = new ArrayList<>();
        if (StringUtils.isNotBlank(roleId)) {
            oldPermissions = this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getRole().getId().equals(roleId))
                    .map(this.permissionRelationFormatter::format)
                    .toList();
        }
        var newPermissions = roleModel.getPermissionList();
        this.checkPermissionRelation(request, oldPermissions, newPermissions, SystemPermissionEnum.SUPER_ADMIN, SystemPermissionEnum.ORGANIZE_OWNER);

        for (var permissionModel : newPermissions) {
            var permission = SystemPermissionEnum.parse(permissionModel.getPermission());
            if (!permission.getIsSuperAdmin() && !permission.getIsOrganizePermission()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown permission type");
            }
        }

        for (var permissionModel : roleModel.getPermissionList()) {
            var organizeId = Optional.ofNullable(permissionModel)
                    .map(s -> s.getOrganize())
                    .map(s -> s.getId())
                    .orElse(StringUtils.EMPTY);
            var stream = this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getRole().getName().equals(roleName))
                    .where(s -> s.getRole().getIsDeleted().equals(false));

            if (StringUtils.isNotBlank(roleId)) {
                stream = stream.where(s -> !s.getRole().getId().equals(roleId));
            }

            if (StringUtils.isNotBlank(organizeId)) {
                stream = stream.where(s -> s.getOrganize().getId().equals(organizeId));
            }

            if (stream.exists()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name cannot be duplicated");
            }
        }
    }

    protected void checkPermissionRelation(HttpServletRequest request, UserModel user) {
        if (ObjectUtil.isNull(user.getRoleList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be null");
        }

        var userId = Optional.ofNullable(user)
                .map(s -> s.getId())
                .orElse(StringUtils.EMPTY);
        List<PermissionRelationModel> oldPermissions = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            oldPermissions = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .findOne()
                    .map(this.userFormatter::formatWithMoreInformation)
                    .map(this.permissionUtil::convertToPermissionList)
                    .get();
        }
        var newPermissions = this.permissionUtil.convertToPermissionList(user);
        this.checkPermissionRelation(request, oldPermissions, newPermissions, SystemPermissionEnum.SUPER_ADMIN, SystemPermissionEnum.ORGANIZE_OWNER);
    }

    private void checkPermissionRelation(HttpServletRequest request, List<PermissionRelationModel> oldPermissions, List<PermissionRelationModel> newPermissions, SystemPermissionEnum... permissionList) {
        for (var permissionModel : newPermissions) {
            var organizeId = Optional.ofNullable(permissionModel)
                    .map(s -> s.getOrganize())
                    .map(s -> s.getId())
                    .orElse(StringUtils.EMPTY);
            this.checkNotBlankOfPermission(permissionModel.getPermission());

            if (ObjectUtil.isNull(EnumUtil.getBy(SystemPermissionEnum::getValue, permissionModel.getPermission()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission does not exist");
            }

            if (StringUtils.isBlank(organizeId) && !SystemPermissionEnum.parse(permissionModel.getPermission()).getIsSuperAdmin()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permissions must be associated with an organization");
            }

            if (StringUtils.isNotBlank(organizeId) && SystemPermissionEnum.parse(permissionModel.getPermission()).getIsSuperAdmin()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permissions must not be associated with an organization");
            }
        }

        for (var oldPermissionModel : oldPermissions) {
            var organizeId = Optional.ofNullable(oldPermissionModel)
                    .map(s -> s.getOrganize())
                    .map(s -> s.getId())
                    .orElse(StringUtils.EMPTY);
            var permission = oldPermissionModel.getPermission();
            if (JinqStream.from(newPermissions)
                    .where(s -> StringUtils.equals(s.getPermission(), permission))
                    .where(s -> StringUtils.equals(
                            Optional.ofNullable(s)
                                    .map(m -> m.getOrganize())
                                    .map(m -> m.getId())
                                    .orElse(StringUtils.EMPTY),
                            organizeId
                    ))
                    .exists()
            ) {
                continue;
            }

            this.permissionUtil.checkAnyPermission(request, organizeId, permissionList);
        }
        for (var newPermissionModel : newPermissions) {
            var organizeId = Optional.ofNullable(newPermissionModel)
                    .map(s -> s.getOrganize())
                    .map(s -> s.getId())
                    .orElse(StringUtils.EMPTY);
            var permission = newPermissionModel.getPermission();
            if (JinqStream.from(oldPermissions)
                    .where(s -> StringUtils.equals(s.getPermission(), permission))
                    .where(s -> StringUtils.equals(
                            Optional.ofNullable(s)
                                    .map(m -> m.getOrganize())
                                    .map(m -> m.getId())
                                    .orElse(StringUtils.EMPTY),
                            organizeId
                    ))
                    .exists()
            ) {
                continue;
            }

            this.permissionUtil.checkAnyPermission(request, organizeId, permissionList);
        }
    }
}
