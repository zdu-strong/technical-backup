package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class PermissionRelationModel {

    private String id;
    private Date createDate;
    private Date updateDate;
    private String permission;
    private RoleModel role;
    private OrganizeModel organize;
    private Boolean isOrganizePermission;

}
