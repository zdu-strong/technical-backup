package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.util.EnumUtil;
import com.john.project.enums.OrganizeTypeEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.service.OrganizeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public abstract class ValidationFieldUtilValidEnumValue extends ValidationFieldUtilMustBeNull {

    @Autowired
    private OrganizeService organizeService;

    public void checkValidOfOrganizeType(OrganizeModel organizeModel) {
        if (StringUtils.isBlank(organizeModel.getOrganizeType())) {
            return;
        }
        if (EnumUtil.getBy(OrganizeTypeEnum::getValue, organizeModel.getOrganizeType()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect organizeType");
        }

        if (Optional.ofNullable(organizeModel.getParent())
                .filter(s -> StringUtils.isNotBlank(s.getId()))
                .isPresent()) {
            if (List.of(OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE).contains(OrganizeTypeEnum.parse(organizeModel.getOrganizeType()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect organizeType");
            }
        }
        if (Optional.ofNullable(organizeModel.getParent())
                .filter(s -> StringUtils.isNotBlank(s.getId()))
                .isPresent()) {
            if (OrganizeTypeEnum.parse(organizeModel.getOrganizeType()) == OrganizeTypeEnum.GOVERNANCE_REGION) {
                var topOrganizeModel = this.organizeService.getById(organizeModel.getParent().getId());
                if (!List.of(OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE).contains(OrganizeTypeEnum.parse(topOrganizeModel.getOrganizeType()))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect organizeType");
                }
            }
        }
        if (Optional.ofNullable(organizeModel.getParent())
                .filter(s -> StringUtils.isNotBlank(s.getId()))
                .isEmpty()) {
            if (!List.of(OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE, OrganizeTypeEnum.ORGANIZE).contains(OrganizeTypeEnum.parse(organizeModel.getOrganizeType()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect organizeType");
            }
        }
    }

}
