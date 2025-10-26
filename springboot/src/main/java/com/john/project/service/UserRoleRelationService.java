package com.john.project.service;

import java.util.Date;

import com.john.project.entity.RoleEntity;
import com.john.project.entity.UserEntity;
import com.john.project.entity.UserRoleRelationEntity;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.SystemRoleEnum;

@Service
public class UserRoleRelationService extends BaseService {

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private PermissionRelationService permissionRelationService;

    public void create(String userId, String userRoleId) {
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(userRoleId))
                .getOnlyValue();

        var userRoleRelationEntity = new UserRoleRelationEntity();
        userRoleRelationEntity.setId(newId());
        userRoleRelationEntity.setCreateDate(new Date());
        userRoleRelationEntity.setUpdateDate(new Date());
        userRoleRelationEntity.setUser(userEntity);
        userRoleRelationEntity.setRole(roleEntity);
        this.persist(userRoleRelationEntity);
    }

    public boolean refresh() {
        return this.createDefaultUserRoleList();
    }


    private boolean createDefaultUserRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var roleName = systemRoleEnum.getValue();
            if (this.streamAll(RoleEntity.class)
                    .where(s -> s.getName().equals(roleName))
                    .selectAllList(s -> s.getPermissionRelationList())
                    .where(s -> s.getPermission().getName().equals(roleName))
                    .exists()) {
                if (this.refreshDefaultUserRoleList(systemRoleEnum)) {
                    return true;
                }
                continue;
            }
            var roleModel = new RoleModel().setName(roleName).setPermissionList(systemRoleEnum.getPermissionList().stream().map(s -> new PermissionRelationModel().setPermission(s.getValue())).toList());
            this.roleService.create(roleModel);
            return true;
        }
        return false;
    }

    private boolean refreshDefaultUserRoleList(SystemRoleEnum systemRoleEnum) {
//        var roleName = systemRoleEnum.getValue();
//        var roleList = this.streamAll(RoleEntity.class)
//                .where(s -> s.getIsOrganizeRole().equals(false))
//                .where(s -> s.getName().equals(roleName))
//                .toList();
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
//            for (var permissionEntity : permissionList) {
//                this.remove(permissionEntity);
//            }
//            for (var permissionEnum : systemRoleEnum.getPermissionList()) {
//                this.permissionRelationService.create(roleEntity.getId(), permissionEnum);
//            }
//            return true;
//        }

        return false;
    }

}
