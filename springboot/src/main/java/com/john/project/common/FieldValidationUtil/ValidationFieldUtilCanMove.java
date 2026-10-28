package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.OrganizeEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilCanMove extends ValidationFieldUtilCanRecall {

    @Transactional(readOnly = true)
    public void checkCanBeMoveOfOrganize(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return;
        }
        if (id.equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
        if (this.organizeService.isChildOfOrganize(parentId, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (organizeEntity.getIsTopOrganize()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
    }

}
