package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;

import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleTest extends BaseTest {

    private SuperAdminRoleQueryPaginationModel superAdminRoleQueryPaginationModel;

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        var paginationModel = this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel);
        var roleList = paginationModel.getItems();
        assertEquals(1, roleList.size());
        var roleModel = JinqStream.from(roleList).getOnlyValue();
        assertEquals(36, roleModel.getId().length());
        assertEquals(SystemRoleEnum.SUPER_ADMIN.getValue(), roleModel.getName());
        assertNotNull(roleModel.getCreateDate());
        assertNotNull(roleModel.getUpdateDate());
        assertEquals(1, roleModel.getPermissionList().size());
        var permission = JinqStream.from(roleModel.getPermissionList()).getOnlyValue();
        assertEquals(SystemPermissionEnum.SUPER_ADMIN.getValue(), permission.getPermission());
        assertEquals(StringUtils.EMPTY, permission.getOrganize().getId());
    }

    @BeforeEach
    public void beforeEach() {
        superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
        superAdminRoleQueryPaginationModel.setPageNum(1L);
        superAdminRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
    }

}
