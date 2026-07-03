package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class IdentityCardModel {
    private String id;

    /**
     * <p>
     *
     * @see com.john.project.enums.SystemPermissionEnum
     * </p>
     * ANONYMOUS
     * RESIDENT
     * IMMIGRANT
     * CITIZEN
     */
    private String identityType;

    private String address;

    private Date createDate;

    private Date updateDate;

    private UserModel user;

    private OrganizeModel topOrganize;

    private OrganizeModel governanceRegion;

}
