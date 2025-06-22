package com.john.project.format;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.PermissionRelationEntity;
import com.john.project.model.OrganizeModel;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.RoleModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PermissionRelationFormatter extends BaseService {

    public PermissionRelationModel format(PermissionRelationEntity permissionRelationEntity) {
        var permissionModel = new PermissionRelationModel();
        BeanUtils.copyProperties(permissionRelationEntity, permissionModel);
        permissionModel.setPermission(permissionRelationEntity.getPermission().getName());
        permissionModel.setRole(new RoleModel().setId(permissionRelationEntity.getRole().getId()));
        permissionModel.setIsOrganizePermission(ObjectUtil.isNotNull(permissionRelationEntity.getOrganize()));
        if (ObjectUtil.isNotNull(permissionRelationEntity.getOrganize())) {
            permissionModel.setOrganize(this.organizeFormatter.format(permissionRelationEntity.getOrganize()));
        } else {
            permissionModel.setOrganize(new OrganizeModel().setId(StringUtils.EMPTY));
        }
        return permissionModel;
    }

}
