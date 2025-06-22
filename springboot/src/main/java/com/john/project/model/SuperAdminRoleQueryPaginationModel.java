package com.john.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SuperAdminRoleQueryPaginationModel extends PaginationModel<RoleModel> {

    private String organizeId;

    private List<String> permissionList;

}
