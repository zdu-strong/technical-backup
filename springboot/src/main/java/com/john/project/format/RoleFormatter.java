package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.RoleEntity;
import com.john.project.model.RoleModel;

@Service
public class RoleFormatter extends BaseService {

    public RoleModel format(RoleEntity roleEntity) {
        var roleModel = new RoleModel();
        BeanUtils.copyProperties(roleEntity, roleModel);
        var id = roleEntity.getId();

        var permissionList = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getPermissionRelationList())
                .map(this.permissionRelationFormatter::format)
                .toList();
        roleModel.setPermissionList(permissionList);
        return roleModel;
    }

}
