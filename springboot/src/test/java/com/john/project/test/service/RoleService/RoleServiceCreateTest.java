package com.john.project.test.service.RoleService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.john.project.model.PermissionRelationModel;
import com.john.project.model.RoleModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.test.common.BaseTest.BaseTest;

public class RoleServiceCreateTest extends BaseTest {

    private RoleModel roleModel;

    @Test
    public void test() {
        var result = this.roleService.create(roleModel);
        assertTrue(StringUtils.isNotBlank(result.getId()));
    }

    @BeforeEach
    public void beforeEach() {
        roleModel = new RoleModel()
                .setName(uuidUtil.v4())
                .setPermissionList(List.of(new PermissionRelationModel().setPermission(SystemPermissionEnum.SUPER_ADMIN.getValue())));
    }

}

