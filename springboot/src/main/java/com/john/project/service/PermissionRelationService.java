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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.SystemPermissionEnum;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionRelationService extends BaseService {

    @Autowired
    @Lazy
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

}
