package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.enums.OrganizeTypeEnum;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterMoveOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
        superAdminRoleQueryPaginationModel.setPageNum(1L);
        superAdminRoleQueryPaginationModel.setPageSize(100L);
        superAdminRoleQueryPaginationModel.setOrganizeId(organizeId);
        var roleList = this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel).getItems();
        assertEquals(2, roleList.size());
        assertTrue(
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getPermissionList())
                        .where(s -> ObjectUtil.equals(s.getOrganize().getId(), organizeId))
                        .where(s -> ObjectUtil.equals(s.getPermission(), SystemPermissionEnum.ORGANIZE_OWNER.getValue()))
                        .exists());
        assertTrue(
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getPermissionList())
                        .where(s -> ObjectUtil.equals(s.getOrganize().getId(), organizeId))
                        .where(s -> ObjectUtil.equals(s.getPermission(), SystemPermissionEnum.ORGANIZE_MEMBER.getValue()))
                        .exists());
    }

    @BeforeEach
    public void beforeEach() {
        this.systemInitScheduled.scheduled();
        var organizeModel = new OrganizeModel()
                .setName("Super Saiyan Son Goku")
                .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
        var parent = this.organizeUtil.create(organizeModel);
        this.initOrganizeRoleList(parent);
        var childOrganize = this.organizeUtil.create(new OrganizeModel().setName("Son Gohan").setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue()).setParent(parent));
        this.organizeId = childOrganize.getId();
        this.initOrganizeRoleList(childOrganize);
        this.organizeUtil.move(organizeId, null);
    }

}
