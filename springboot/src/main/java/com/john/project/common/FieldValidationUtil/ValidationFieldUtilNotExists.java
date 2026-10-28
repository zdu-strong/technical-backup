package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilNotExists extends ValidationFieldUtilExists {

    @Transactional(readOnly = true)
    public void checkNotExistsOfUserByUsername(String username) {
        if (this.streamAll(UserEntity.class)
                .where(s -> s.getUsernameLowerCase().equals(username.toLowerCase()))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already used");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotExistsOfUserByUsername(String username, String userId) {
        if (this.streamAll(UserEntity.class)
                .where(s -> s.getUsernameLowerCase().equals(username.toLowerCase()))
                .where(s -> !s.getId().equals(userId))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already used");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotExistsOfOrganizeByParentOrganize(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getIsTopOrganize())
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot has parent organize");
        }
    }

}
