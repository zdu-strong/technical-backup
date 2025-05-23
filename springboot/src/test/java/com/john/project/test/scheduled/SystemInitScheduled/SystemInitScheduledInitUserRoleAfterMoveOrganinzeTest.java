package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitUserRoleAfterMoveOrganinzeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var roleList = this.roleOrganizeRelationService
                .searchOrganizeRoleForSuperAdminByPagination(1, SystemRoleEnum.values().length, organizeId, false)
                .getItems();
        assertEquals(2, roleList.size());
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName())).toList()
                .contains(SystemRoleEnum.ORGANIZE_VIEW));
        assertTrue(JinqStream.from(roleList).map(s -> SystemRoleEnum.parse(s.getName())).toList()
                .contains(SystemRoleEnum.ORGANIZE_MANAGE));
        assertEquals(this.organizeId,
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getOrganizeList())
                        .select(s -> s.getId())
                        .distinct()
                        .getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        this.systemInitScheduled.scheduled();
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parent = this.organizeUtil.create(organizeModel);
        this.organizeId = this.organizeUtil.create(new OrganizeModel().setName("Son Gohan").setParent(parent))
                .getId();
        this.organizeUtil.move(organizeId, null);
    }

}
