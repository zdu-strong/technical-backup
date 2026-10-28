package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilExists extends ValidationFieldUtilNotEdgesSpace {

    @Transactional(readOnly = true)
    public void checkExistsOfRole(String id) {
        if (!this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfUserByUserId(String id) {
        if (!this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfUserByAccount(String account) {
        var userId = account;
        var email = account;
        var username = account;
        if (!this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsDeleted().equals(false))
                .exists() &&
                !this.streamAll(UserEmailEntity.class)
                        .where(s -> s.getEmail().equals(email))
                        .where(s -> !s.getIsDeleted())
                        .where(s -> !s.getUser().getIsDeleted())
                        .exists() &&
                !this.streamAll(UserEntity.class)
                        .where(s -> s.getUsernameLowerCase().equals(username.toLowerCase()))
                        .where(s -> s.getIsDeleted().equals(false))
                        .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username or password");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfUserMessage(String id) {
        var exists = this.streamAll(UserMessageEntity.class).where(s -> s.getId().equals(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User message do not exists");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfLongTermTask(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var hasExist = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfOrganize(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistsOfOrganizeByParentOrganize(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent organize does not exist");
        }
    }

}