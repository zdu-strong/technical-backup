package com.john.project.enums;

import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum OrganizeTypeEnum {

    COUNTRY("COUNTRY"),
    ALLIANCE("ALLIANCE"),
    GOVERNANCE_REGION("GOVERNANCE_REGION"),
    ORGANIZE("ORGANIZE"),
    ;

    private String value;

    public static OrganizeTypeEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(OrganizeTypeEnum::getValue, value)).get();
    }
}