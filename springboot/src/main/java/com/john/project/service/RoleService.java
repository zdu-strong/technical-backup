package com.john.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.john.project.constant.SuperAdminRoleNameConstant;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.model.PaginationModel;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.jpa.JPQL;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private PermissionRelationService permissionRelationService;

    public RoleModel create(RoleModel roleModel) {
        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(roleModel.getName());
        roleEntity.setIsDeleted(false);
        roleEntity.setHasSystemPermission(hasSystemPermission(roleModel));
        roleEntity.setHasOrganizePermission(hasOrganizePermission(roleModel));
        this.persist(roleEntity);

        for (var permission : roleModel.getPermissionList()) {
            this.permissionRelationService.create(roleEntity.getId(), permission.getOrganize(), SystemPermissionEnum.parse(permission.getPermission()));
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
        roleEntity.setHasSystemPermission(hasSystemPermission(roleModel));
        roleEntity.setHasOrganizePermission(hasOrganizePermission(roleModel));
        this.merge(roleEntity);

        var permissionList = this.streamAll(PermissionRelationEntity.class)
                .where(s -> s.getRole().getId().equals(id))
                .toList();

        for (var permissionRelationEntity : permissionList) {
            var permission = permissionRelationEntity.getPermission().getName();
            var organizeId = Optional.ofNullable(permissionRelationEntity)
                    .map(m -> m.getOrganize())
                    .map(m -> m.getId())
                    .orElse(StringUtils.EMPTY);
            if (JinqStream.from(roleModel.getPermissionList())
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
            this.remove(permissionRelationEntity);
        }

        for (var permissionModel : roleModel.getPermissionList()) {
            var organizeId = Optional.ofNullable(permissionModel)
                    .map(m -> m.getOrganize())
                    .map(m -> m.getId())
                    .orElse(StringUtils.EMPTY);
            if (JinqStream.from(permissionList)
                    .where(s -> StringUtils.equals(s.getPermission().getName(), permissionModel.getPermission()))
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
            this.permissionRelationService.create(roleEntity.getId(), permissionModel.getOrganize(), SystemPermissionEnum.parse(permissionModel.getPermission()));
        }

    }

    public void delete(String id) {
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        roleEntity.setIsDeleted(true);
        roleEntity.setUpdateDate(new Date());
        this.merge(roleEntity);
    }

    public RoleModel getRoleById(String id) {
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.roleFormatter.format(roleEntity);
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchRoleForSuperAdminByPagination(SuperAdminRoleQueryPaginationModel query) {
        var stream = this.streamAll(PermissionRelationEntity.class)
                .join(s -> JinqStream.of(s.getRole()))
                .join(s -> JinqStream.of(s.getOne().getPermission()))
                .leftOuterJoin(s -> JinqStream.of(s.getOne().getOne().getOrganize()))
                .select(s -> new Tuple3<>(s.getOne().getOne().getTwo(), s.getOne().getTwo(), s.getTwo()))
                .leftOuterJoinList(s -> s.getThree().getAncestorList())
                .leftOuterJoin(s -> JinqStream.of(s.getTwo().getAncestor()))
                .select(s -> new Tuple3<>(s.getOne().getOne().getOne(), s.getOne().getOne().getTwo(), s.getTwo()))
                .where(s -> !s.getOne().getIsDeleted());

        if (StringUtils.isNotBlank(query.getOrganizeId())) {
            var organizeId = query.getOrganizeId();
            stream = stream.where(s -> s.getThree().getId().equals(organizeId));
        }

        if (CollectionUtils.isNotEmpty(query.getPermissionList())) {
            var permissionList = query.getPermissionList();
            stream = stream.where(s -> permissionList.contains(s.getTwo().getName()));
        }

        if (StringUtils.isNotBlank(query.getRoleName())) {
            var roleName = query.getRoleName();
            stream = stream.where(s -> JPQL.like(s.getOne().getName(), roleName + "%"));
        }

        if (ObjectUtil.isNotNull(query.getIsOnlySystemRole()) && query.getIsOnlySystemRole()) {
            stream = stream.where(s -> s.getOne().getHasSystemPermission());
        }

        var roleStream = stream.group(s -> s.getOne(), (s, t) -> s)
                .select(s -> s.getOne());

        return new PaginationModel<>(query, roleStream, this.roleFormatter::format);
    }

    @Transactional(readOnly = true)
    public void checkExistRoleById(String id) {
        if (!hasExistsRoleById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkRoleRelation(HttpServletRequest request, RoleModel roleModel) {
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
        this.checkCanChangePermissions(request, oldPermissions, newPermissions, SystemPermissionEnum.SUPER_ADMIN, SystemPermissionEnum.ORGANIZE_OWNER);

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

    @Transactional(readOnly = true)
    public void checkCanChangePermissions(HttpServletRequest request, List<PermissionRelationModel> oldPermissions, List<PermissionRelationModel> newPermissions, SystemPermissionEnum... permissionList) {
        for (var permissionModel : newPermissions) {
            var organizeId = Optional.ofNullable(permissionModel)
                    .map(s -> s.getOrganize())
                    .map(s -> s.getId())
                    .orElse(StringUtils.EMPTY);
            this.validationFieldUtil.checkNotBlankOfPermission(permissionModel.getPermission());

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

    public void createDefaultSuperAdminRole() {
        var roleName = SuperAdminRoleNameConstant.SUPER_ADMINISTRATOR;
        var permissionName = SystemPermissionEnum.SUPER_ADMIN.getValue();

        if (!SystemPermissionEnum.SUPER_ADMIN.getIsSuperAdmin()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while initializing the super administrator role");
        }

        if (this.streamAll(PermissionRelationEntity.class)
                .where(s -> s.getPermission().getName().equals(permissionName))
                .where(s -> s.getRole().getIsDeleted().equals(false))
                .exists()) {
            return;
        }

        if (this.streamAll(RoleEntity.class)
                .where(s -> s.getName().equals(roleName))
                .where(s -> s.getIsDeleted().equals(false))
                .exists()
        ) {
            var roleEntity = this.streamAll(RoleEntity.class)
                    .where(s -> s.getName().equals(roleName))
                    .where(s -> s.getIsDeleted().equals(false))
                    .findFirst()
                    .get();
            this.permissionRelationService.create(roleEntity.getId(), null, SystemPermissionEnum.SUPER_ADMIN);
            return;
        }

        var roleModel = new RoleModel().setName(roleName).setPermissionList(List.of(new PermissionRelationModel().setPermission(permissionName)));
        this.create(roleModel);
    }

    private boolean hasExistsRoleById(String id) {
        var exists = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        return exists;
    }

    private boolean hasSystemPermission(RoleModel roleModel) {
        var hasSystemPermission = JinqStream.from(roleModel.getPermissionList())
                .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizePermission())
                .exists();
        return hasSystemPermission;
    }

    private boolean hasOrganizePermission(RoleModel roleModel) {
        var hasOrganizePermission = JinqStream.from(roleModel.getPermissionList())
                .where(s -> SystemPermissionEnum.parse(s.getPermission()).getIsOrganizePermission())
                .exists();
        return hasOrganizePermission;
    }

}
