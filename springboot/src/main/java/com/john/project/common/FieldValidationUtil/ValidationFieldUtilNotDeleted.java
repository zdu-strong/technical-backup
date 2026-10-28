package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.entity.UserEntity;
import com.john.project.model.OrganizeModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public abstract class ValidationFieldUtilNotDeleted extends ValidationFieldUtilPermissionRelation {

    @Transactional(readOnly = true)
    public void checkNotDeletedOfRole(String id) {
        if (!this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getIsDeleted().equals(false))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotDeletedOfUser(String id) {
        if (!this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getIsDeleted().equals(false))
                .exists()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkNotDeletedOfOrganize(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .findOne()
                .filter(s -> this.organizeFormatter.isActive(s))
                .isPresent();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }


    @Transactional(readOnly = true)
    public void checkNotDeletedOfOrganizeByParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = Optional.ofNullable(organizeModel.getParent())
                .map(s -> s.getId())
                .orElse(null);
        if (StringUtils.isBlank(parentOrganizeId)) {
            return;
        }
        this.checkNotDeletedOfOrganize(parentOrganizeId);
    }

}
