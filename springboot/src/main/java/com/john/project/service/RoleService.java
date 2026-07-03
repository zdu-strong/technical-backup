package com.john.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.database.JPQLFunction;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.entity.UserEntity;
import com.john.project.model.PaginationModel;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.jpa.JPQL;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
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

        for (var permission : permissionList) {
            if (JinqStream.from(roleModel.getPermissionList())
                    .where(s -> ObjectUtil.equals(permission.getPermission(), s.getPermission()))
                    .where(s -> ObjectUtil.equals(
                            Optional.ofNullable(permission.getOrganize()).map(m -> m.getId()).orElse(null),
                            Optional.ofNullable(s.getOrganize())
                                    .map(m -> Optional.ofNullable(m.getId()))
                                    .filter(m -> m.isPresent())
                                    .map(m -> m.get())
                                    .filter(StringUtils::isNotBlank)
                                    .orElse(null)

                    ))
                    .exists()
            ) {
                continue;
            }
            this.remove(permission);
        }

        for (var permission : roleModel.getPermissionList()) {
            if (JinqStream.from(permissionList)
                    .where(s -> ObjectUtil.equals(permission.getPermission(), s.getPermission()))
                    .where(s -> ObjectUtil.equals(
                            Optional.ofNullable(permission.getOrganize())
                                    .map(m -> Optional.ofNullable(m.getId()))
                                    .filter(m -> m.isPresent())
                                    .map(m -> m.get())
                                    .filter(StringUtils::isNotBlank)
                                    .orElse(null),
                            Optional.ofNullable(s.getOrganize()).map(m -> m.getId()).orElse(null)
                    ))
                    .exists()
            ) {
                continue;
            }
            this.permissionRelationService.create(roleEntity.getId(), permission.getOrganize(), SystemPermissionEnum.parse(permission.getPermission()));
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
    public void checkCanCreateRole(RoleModel roleModel, HttpServletRequest request) {
        var roleName = roleModel.getName();

        for (var permission : roleModel.getPermissionList()) {
            this.validationFieldUtil.checkNotBlankOfPermission(permission.getPermission());
        }

        if (JinqStream.from(roleModel.getPermissionList())
                .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .where(s -> s.getOrganize() != null && StringUtils.isNotBlank(s.getOrganize().getId()))
                .exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System permission cannot be associated with organization identifiers");
        }

        if (JinqStream.from(roleModel.getPermissionList())
                .where(s -> SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .where(s -> s.getOrganize() == null || StringUtils.isBlank(s.getOrganize().getId()))
                .exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize permission must be associated with organization identifiers");
        }

        if (roleModel.getPermissionList().stream().anyMatch(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())) {
            if (this.streamAll(RoleEntity.class).anyMatch(s -> s.getName().equals(roleName))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name cannot be duplicated");
            }
        }

        for (var permissionModel : roleModel.getPermissionList()) {
            if (!SystemPermissionEnum.parse((permissionModel.getPermission())).getIsOrganizeRole()) {
                continue;
            }

            var organizeId = permissionModel.getOrganize().getId();

            if (this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getOrganize().getId().equals(organizeId))
                    .where(s -> s.getRole().getName().equals(roleName))
                    .exists()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name cannot be duplicated");
            }
        }

        if (!this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
            if (roleModel.getPermissionList().stream().anyMatch(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only the super administrator can assign system permission");
            }
        }

        if (!this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN, SystemPermissionEnum.ORGANIZE_MANAGE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only administrators can create roles");
        }
    }

    @Transactional(readOnly = true)
    public void checkCanUpdateRole(RoleModel roleModel, HttpServletRequest request) {
        var roleName = roleModel.getName();
        var roleId = roleModel.getId();
        var roleEntity = this.streamAll(RoleEntity.class).where(s -> s.getId().equals(roleId)).getOnlyValue();
        var roleModelInDatabase = this.roleFormatter.format(roleEntity);

        for (var permission : roleModel.getPermissionList()) {
            this.validationFieldUtil.checkNotBlankOfPermission(permission.getPermission());
        }

        if (JinqStream.from(roleModel.getPermissionList())
                .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .where(s -> s.getOrganize() != null && StringUtils.isNotBlank(s.getOrganize().getId()))
                .exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System permission cannot be associated with organization identifiers");
        }

        if (JinqStream.from(roleModel.getPermissionList())
                .where(s -> SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .where(s -> s.getOrganize() == null || StringUtils.isBlank(s.getOrganize().getId()))
                .exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize permission must be associated with organization identifiers");
        }


        if (roleModel.getPermissionList().stream().anyMatch(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())) {
            if (this.streamAll(RoleEntity.class).anyMatch(s -> s.getName().equals(roleName) && !s.getId().equals(roleId) && s.getIsDeleted().equals(false))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name cannot be duplicated");
            }
        }

        for (var permissionModel : roleModel.getPermissionList()) {
            if (!SystemPermissionEnum.parse((permissionModel.getPermission())).getIsOrganizeRole()) {
                continue;
            }

            var organizeId = permissionModel.getOrganize().getId();

            if (this.streamAll(PermissionRelationEntity.class)
                    .where(s -> s.getOrganize().getId().equals(organizeId))
                    .where(s -> s.getRole().getName().equals(roleName))
                    .where(s -> !s.getRole().getId().equals(roleId))
                    .where(s -> s.getRole().getIsDeleted().equals(false))
                    .exists()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name cannot be duplicated");
            }
        }

        if (!this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
            for (var permission : roleModel.getPermissionList()) {
                if (SystemPermissionEnum.parse(permission.getPermission()).getIsOrganizeRole()) {
                    continue;
                }
                if (!JinqStream.from(roleModelInDatabase.getPermissionList())
                        .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                        .where(s -> s.getPermission().equals(permission.getPermission()))
                        .exists()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only the super administrator can assign system permission");
                }
            }
        }


        if (!this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
            for (var permission : roleModelInDatabase.getPermissionList()) {
                if (SystemPermissionEnum.parse(permission.getPermission()).getIsOrganizeRole()) {
                    continue;
                }
                if (!JinqStream.from(roleModel.getPermissionList())
                        .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                        .where(s -> s.getPermission().equals(permission.getPermission()))
                        .exists()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only the super administrator can remove system permission");
                }
            }
        }

        if (!this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN, SystemPermissionEnum.ORGANIZE_MANAGE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only administrators can update roles");
        }
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

    private boolean hasExistsRoleById(String id) {
        var exists = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        return exists;
    }

    private boolean hasSystemPermission(RoleModel roleModel) {
        var hasSystemPermission = JinqStream.from(roleModel.getPermissionList())
                .where(s -> !SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .exists();
        return hasSystemPermission;
    }

    private boolean hasOrganizePermission(RoleModel roleModel) {
        var hasOrganizePermission = JinqStream.from(roleModel.getPermissionList())
                .where(s -> SystemPermissionEnum.parse(s.getPermission()).getIsOrganizeRole())
                .exists();
        return hasOrganizePermission;
    }

//    @Transactional(readOnly = true)
//    public void checkCanCreateOrganizeRole(RoleModel roleModel, HttpServletRequest request) {
//        if (!roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
//                .filter(m -> m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
//            return;
//        }
//        if (roleModel.getPermissionList().stream().anyMatch(s -> Arrays.stream(SystemPermissionEnum.values())
//                .filter(m -> !m.getIsOrganizeRole()).map(m -> m.getValue()).toList().contains(s))) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot have system permissions");
//        }
//        if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
//            return;
//        }
//        for (var permissionName : roleModel.getPermissionList()) {
//            for (var organizeModel : roleModel.getOrganizeList()) {
//                this.permissionUtil.checkAnyPermission(request, organizeModel.getId(),
//                        SystemPermissionEnum.parse(permissionName));
//            }
//        }
//    }

}
