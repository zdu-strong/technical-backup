package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterCreateOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
        superAdminRoleQueryPaginationModel.setPageNum(1L);
        superAdminRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
        superAdminRoleQueryPaginationModel.setOrganizeId(organizeId);
        var roleList = this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel).getItems();
        assertEquals(2, roleList.size());
        assertTrue(
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getPermissionList())
                        .where(s -> ObjectUtil.equals(s.getOrganize().getId(), organizeId))
                        .where(s -> ObjectUtil.equals(s.getPermission(), SystemRoleEnum.ORGANIZE_MANAGE.getValue()))
                        .exists());
        assertTrue(
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getPermissionList())
                        .where(s -> ObjectUtil.equals(s.getOrganize().getId(), organizeId))
                        .where(s -> ObjectUtil.equals(s.getPermission(), SystemRoleEnum.ORGANIZE_VIEW.getValue()))
                        .exists());
    }

    @BeforeEach
    public void beforeEach() {
        this.systemInitScheduled.scheduled();
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeUtil.create(organizeModel).getId();
    }

}
