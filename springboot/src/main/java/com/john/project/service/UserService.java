package com.john.project.service;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.john.project.entity.UserEmailEntity;
import com.john.project.entity.UserEntity;
import com.john.project.model.SuperAdminUserQueryPaginationModel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.PaginationModel;
import com.john.project.model.UserModel;

@Service
public class UserService extends BaseService {

    @Autowired
    @Lazy
    private UserRoleRelationService userRoleRelationService;

    @Autowired
    @Lazy
    private UserEmailService userEmailService;

    @Autowired
    @Lazy
    private TokenService tokenService;

    @SneakyThrows
    public UserModel create(UserModel userModel) {
        var userEntity = new UserEntity();
        userEntity.setId(newId());
        userEntity.setUsername(userModel.getUsername());
        userEntity.setUsernameLowerCase(userEntity.getUsername().toLowerCase());
        userEntity.setPassword(this.tokenService.getPasswordInDatabaseOfEncryptedPassword(userModel.getPassword(), userEntity.getId()));
        userEntity.setIsDeleted(false);
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        this.persist(userEntity);

        for (var userEmailModel : userModel.getUserEmailList()) {
            this.userEmailService.createUserEmail(userEmailModel.getEmail(), userEntity.getId());
        }

        for (var roleModel : userModel.getRoleList()) {
            this.userRoleRelationService.create(userEntity.getId(), roleModel.getId());
        }

        var accessToken = this.tokenService.generateAccessToken(userEntity.getId(), userModel.getPassword());
        this.tokenService.deleteTokenEntity(JWT.decode(accessToken).getId());

        return this.userFormatter.formatWithMoreInformation(userEntity);
    }

    public void update(UserModel userModel) {
        var userId = userModel.getId();
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        userEntity.setUsername(userModel.getUsername());
        userEntity.setUsernameLowerCase(userModel.getUsername().toLowerCase());

        if (StringUtils.isNotBlank(userModel.getPassword())) {
            userEntity.setPassword(this.tokenService.getPasswordInDatabaseOfEncryptedPassword(userModel.getPassword(), userEntity.getId()));
            var accessToken = this.tokenService.generateAccessToken(userEntity.getId(), userModel.getPassword());
            this.tokenService.deleteTokenEntity(JWT.decode(accessToken).getId());
        }

        this.merge(userEntity);

        var userRoleRelationList = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .selectAllList(s -> s.getUserRoleRelationList())
                .toList();
        for (var userRoleRelationEntity : userRoleRelationList) {
            if (JinqStream.from(userRoleRelationList)
                    .where(s -> StringUtils.equals(s.getRole().getId(), userRoleRelationEntity.getRole().getId()))
                    .exists()
            ) {
                continue;
            }
            this.remove(userRoleRelationEntity);
        }

        for (var roleModel : userModel.getRoleList()) {
            if (JinqStream.from(userRoleRelationList)
                    .where(s -> StringUtils.equals(s.getRole().getId(), roleModel.getId()))
                    .exists()
            ) {
                continue;
            }
            this.userRoleRelationService.create(userId, roleModel.getId());
        }
    }

    @Transactional(readOnly = true)
    public UserModel getUserWithMoreInformation(String id) {
        var user = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        return this.userFormatter.formatWithMoreInformation(user);
    }

    @Transactional(readOnly = true)
    public UserModel getUser(String id) {
        var user = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    @Transactional(readOnly = true)
    public String getUserId(String account) {
        {
            var userId = account;
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .where(s -> !s.getIsDeleted())
                    .findOne()
                    .orElse(null);
            if (userEntity != null) {
                return userEntity.getId();
            }
        }
        {
            var username = account;
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getUsername().equals(username))
                    .where(s -> !s.getIsDeleted())
                    .findOne()
                    .orElse(null);
            if (userEntity != null) {
                return userEntity.getId();
            }
        }
        {
            var email = account;
            var userEntity = this.streamAll(UserEmailEntity.class)
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> !s.getIsDeleted())
                    .where(s -> !s.getUser().getIsDeleted())
                    .select(s -> s.getUser())
                    .getOnlyValue();
            return userEntity.getId();
        }
    }

    public void delete(String id) {
        var user = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        user.setIsDeleted(true);
        user.setUpdateDate(new Date());
        this.merge(user);

        this.userEmailService.deleteUserEmailByUserId(id);
        this.tokenService.deleteTokenByUserId(id);
    }

    @Transactional(readOnly = true)
    public PaginationModel<UserModel> searchForSuperAdminByPagination(SuperAdminUserQueryPaginationModel query) {
        var stream = this.streamAll(UserEntity.class)
                .where(s -> !s.getIsDeleted())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(query, stream, this.userFormatter::format);
    }

}
