package com.john.project.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.database.JPQLFunction;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.model.PaginationModel;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.jpa.JPQL;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.jinq.tuples.Tuple3;
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
    private PermissionRelationService permissionRelationService;

    public RoleModel create(RoleModel roleModel) {
        var roleEntity = new RoleEntity();
        roleEntity.setId(newId());
        roleEntity.setCreateDate(new Date());
        roleEntity.setUpdateDate(new Date());
        roleEntity.setName(roleModel.getName());
        roleEntity.setIsDeleted(false);
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

    @Transactional(readOnly = true)
    public void checkCanCreateRole(RoleModel roleModel, HttpServletRequest request) {
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

        var roleStream = stream.group(s -> s.getOne(), (s, t) -> s)
                .select(s -> s.getOne());

        return new PaginationModel<>(query, roleStream, this.roleFormatter::format);
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
