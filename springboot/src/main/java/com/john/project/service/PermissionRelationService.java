package com.john.project.service;

import java.util.Date;
import java.util.Optional;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.PermissionEntity;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.RoleModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.SystemPermissionEnum;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionRelationService extends BaseService {

    @Autowired
    private RoleService roleService;

    public void create(String roleId, OrganizeModel organize, SystemPermissionEnum permissionEnum) {
        var permissionName = permissionEnum.getValue();
        var organizeId = Optional.ofNullable(organize)
                .map(s -> Optional.ofNullable(s.getId()))
                .filter(s -> s.isPresent())
                .map(s -> s.get())
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        var permissionEntity = this.streamAll(PermissionEntity.class)
                .where(s -> s.getName().equals(permissionName))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(roleId))
                .getOnlyValue();
        var organizeEntity = Optional.ofNullable(organizeId)
                .filter(StringUtils::isNotBlank)
                .map(m -> {
                    return this.streamAll(OrganizeEntity.class)
                            .where(s -> s.getId().equals(organizeId))
                            .getOnlyValue();
                })
                .orElse(null);

        var rolePermissionRelationEntity = new PermissionRelationEntity();
        rolePermissionRelationEntity.setId(newId());
        rolePermissionRelationEntity.setCreateDate(new Date());
        rolePermissionRelationEntity.setUpdateDate(new Date());
        rolePermissionRelationEntity.setPermission(permissionEntity);
        rolePermissionRelationEntity.setRole(roleEntity);
        rolePermissionRelationEntity.setOrganize(organizeEntity);
        this.persist(rolePermissionRelationEntity);
    }


    @Transactional(readOnly = true)
    public boolean hasNeededToRefresh(String organizeId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            var roleName = systemRoleEnum.getValue();
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            if (this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getOrganize().getId().equals(organizeId))
                    .where(s -> s.getRole().getName().equals(roleName))
                    .exists()) {
                if (this.hasNeededToRefreshDefaultOrganizeRoleList(organizeId, systemRoleEnum)) {
                    return true;
                }
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean refresh(String organizeId) {
        return this.createDefaultOrganizeRoleList(organizeId);
    }

    private boolean createDefaultOrganizeRoleList(String organizeId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            var roleName = systemRoleEnum.getValue();
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            if (this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getOrganize().getId().equals(organizeId))
                    .where(s -> s.getRole().getName().equals(roleName))
                    .exists()) {
                if (this.refreshDefaultOrganizeRoleList(organizeId, systemRoleEnum)) {
                    return true;
                }
                continue;
            }
            var roleModel = new RoleModel()
                    .setName(roleName)
                    .setPermissionList(systemRoleEnum.getPermissionList()
                            .stream()
                            .map(s -> new PermissionRelationModel()
                                    .setPermission(s.getValue())
                                    .setOrganize(new OrganizeModel().setId(organizeId))
                            ).toList()
                    );
            this.roleService.create(roleModel);
            return true;
        }
        return false;
    }

    private boolean hasNeededToRefreshDefaultOrganizeRoleList(String organizeId, SystemRoleEnum systemRoleEnum) {
        var roleName = systemRoleEnum.getValue();
        var roleList = this.streamAll(PermissionRelationEntity.class)
                .where(s -> s.getOrganize().getId().equals(organizeId))
                .where(s -> s.getRole().getName().equals(roleName))
                .select(s -> s.getRole())
                .toList();
//        for (var roleEntity : roleList) {
//            var roleId = roleEntity.getId();
//            var permissionList = this.streamAll(PermissionRelationEntity.class)
//                    .where(s -> s.getRole().getId().equals(roleId))
//                    .toList();
//            if (systemRoleEnum.getPermissionList().size() == permissionList.size()
//                    && systemRoleEnum.getPermissionList().stream().allMatch(m -> permissionList.stream()
//                    .anyMatch(n -> m.getValue().equals(n.getPermission().getName())))) {
//                continue;
//            }
//            return true;
//        }

        return false;
    }

    private boolean refreshDefaultOrganizeRoleList(String organizeId, SystemRoleEnum systemRoleEnum) {
        var roleName = systemRoleEnum.getValue();
        var roleList = this.streamAll(PermissionRelationEntity.class)
                .where(s -> s.getOrganize().getId().equals(organizeId))
                .where(s -> s.getRole().getName().equals(roleName))
                .select(s -> s.getRole())
                .toList();
        for (var roleEntity : roleList) {
            var roleId = roleEntity.getId();
            var permissionList = this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getRole().getId().equals(roleId))
                    .toList();
            if (systemRoleEnum.getPermissionList().size() == permissionList.size()
                    && systemRoleEnum.getPermissionList().stream().allMatch(m -> permissionList.stream()
                    .anyMatch(n -> m.getValue().equals(n.getPermission().getName())))) {
                continue;
            }
            for (var permissionEntity : permissionList) {
                this.remove(permissionEntity);
            }
            for (var permissionEnum : systemRoleEnum.getPermissionList()) {
//                this.create();
//                this.permissionRelationService.create(roleEntity.getId(), permissionEnum);
            }
            return true;
        }

        return false;
    }

}
