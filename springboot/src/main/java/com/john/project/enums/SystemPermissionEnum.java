package com.john.project.enums;

import java.util.List;
import java.util.Optional;

import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemPermissionEnum {

    /* super admin */
    SUPER_ADMIN("SUPER_ADMIN", true, false, false, List.of()),

    /* organize permission */
    ORGANIZE_OWNER("ORGANIZE_OWNER", false, true, false, List.of()),
    ORGANIZE_MEMBER("ORGANIZE_MEMBER", false, true, false, List.of()),

    /* community supervisor */
    COMMUNITY_SUPERVISOR("COMMUNITY_SUPERVISOR", false, true, false, List.of()),
    NATIONAL_SUPERVISORY_COUNCIL("NATIONAL_SUPERVISORY_COUNCIL", false, true, false, List.of()),
    STATE_SUPERVISORY_COUNCIL("STATE_SUPERVISORY_COUNCIL", false, true, false, List.of()),

    /* Identity Card Permission */
    ANONYMOUS("ANONYMOUS", false, false, true, List.of(OrganizeTypeEnum.COUNTRY)),
    RESIDENT("RESIDENT", false, false, true, List.of(OrganizeTypeEnum.COUNTRY)),
    IMMIGRANT("IMMIGRANT", false, false, true, List.of(OrganizeTypeEnum.COUNTRY)),
    CITIZEN("CITIZEN", false, false, true, List.of(OrganizeTypeEnum.COUNTRY)),
    ;

    private String value;
    private Boolean isSystemPermission;
    private Boolean isOrganizePermission;
    private Boolean isIdentityCardPermission;
    private List<OrganizeTypeEnum> organizeType;

    public static SystemPermissionEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(SystemPermissionEnum::getValue, value)).get();
    }
}