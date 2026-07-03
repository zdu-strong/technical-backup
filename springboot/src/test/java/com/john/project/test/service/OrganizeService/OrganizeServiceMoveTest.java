package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.john.project.enums.OrganizeTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceMoveTest extends BaseTest {

    private String organizeId;
    private String parentOrganizeId;

    @Test
    public void test() {
        this.organizeUtil.move(organizeId, parentOrganizeId);
        var result = this.organizeService.getById(organizeId);
        assertEquals(this.parentOrganizeId, result.getParent().getId());
        assertEquals(36, result.getId().length());
        assertEquals("Son Gohan", result.getName());
        assertEquals(0, result.getChildList().size());
        assertEquals(0, result.getChildCount());
        assertEquals(0, result.getDescendantCount());
        assertEquals(1, result.getLevel());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel()
                    .setName("Super Saiyan Son Goku")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue()).setParent(parentOrganize);
            var childOrganize = this.organizeUtil.create(childOrganizeModel);
            this.organizeId = childOrganize.getId();
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo").setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            this.parentOrganizeId = parentOrganize.getId();
        }
    }

}
