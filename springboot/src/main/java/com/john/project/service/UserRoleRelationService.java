package com.john.project.service;

import java.util.Date;

import com.john.project.entity.RoleEntity;
import com.john.project.entity.UserEntity;
import com.john.project.entity.UserRoleRelationEntity;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;

@Service
public class UserRoleRelationService extends BaseService {

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

}
