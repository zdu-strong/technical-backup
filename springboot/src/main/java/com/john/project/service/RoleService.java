package com.john.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.entity.RoleEntity;
import com.john.project.entity.RolePermissionRelationEntity;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.RoleModel;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RoleService extends BaseService {

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

    @Autowired
    private RoleOrganizeRelationService roleOrganizeRelationService;

    public RoleModel create(String role, List<SystemPermissionEnum> permissionList, List<String> organizeIdList) {
        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(role);
        roleEntity.setIsDeleted(false);
        roleEntity.setDeletionCode(StringUtils.EMPTY);
        roleEntity.setIsOrganizeRole(!organizeIdList.isEmpty());
        this.persist(roleEntity);

        for (var permissionEnum : permissionList) {
            this.rolePermissionRelationService.create(roleEntity.getId(), permissionEnum);
        }

        for (var organizeId : organizeIdList) {
            this.roleOrganizeRelationService.create(roleEntity.getId(), organizeId);
        }

        return this.roleFormatter.format(roleEntity);
    }

    public void update(RoleModel roleModel) {
        var id = roleModel.getId();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        roleEntity.setName(roleModel.getName());
        roleEntity.setUpdateDate(new Date());
        this.merge(roleEntity);

        var rolePermissionRelationList = this.streamAll(RolePermissionRelationEntity.class)
                .where(s -> s.getRole().getId().equals(id))
                .toList();

        for (var rolePermissionRelationEntity : rolePermissionRelationList) {
            if (roleModel.getPermissionList().contains(rolePermissionRelationEntity.getPermission().getName())) {
                continue;
            }
            this.remove(rolePermissionRelationEntity);
        }

        for (var permissionEnum : roleModel.getPermissionList().stream()
                .map(s -> SystemPermissionEnum.parse(s))
                .toList()) {
            if (JinqStream.from(rolePermissionRelationList)
                    .select(s -> s.getPermission().getName())
                    .toList()
                    .contains(permissionEnum.getValue())) {
                continue;
            }
            this.rolePermissionRelationService.create(id, permissionEnum);
        }

    }

    public void delete(String id) {
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        roleEntity.setIsDeleted(true);
        roleEntity.setDeletionCode(uuidUtil.v4());
        roleEntity.setUpdateDate(new Date());
        this.merge(roleEntity);
    }

    @Transactional(readOnly = true)
    public void checkCanCreateUserRole(RoleModel roleModel, HttpServletRequest request) {
        if (!roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> !m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            return;
        }
        if (roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot have organization permissions");
        }
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);
    }

    @Transactional(readOnly = true)
    public void checkCanCreateOrganizeRole(RoleModel roleModel, HttpServletRequest request) {
        if (!roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            return;
        }
        if (roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
                .filter(m -> !m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot have system permissions");
        }
        if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
            return;
        }
        for (var permissionName : roleModel.getPermissionList()) {
            for (var organizeModel : roleModel.getOrganizeList()) {
                this.permissionUtil.checkAnyPermission(request, organizeModel.getId(),
                        SystemPermissionEnum.parse(permissionName));
            }
        }
    }

    @Transactional(readOnly = true)
    public void fillOfOrganizeList(RoleModel roleModel) {
        if (ObjectUtil.isEmpty(roleModel.getOrganizeList())) {
            roleModel.setOrganizeList(List.of());
        }
    }

}
