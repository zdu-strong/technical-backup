package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.john.project.enums.OrganizeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceGetByIdGetParentOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var result = this.organizeService.getById(organizeId);
        assertNotNull(result.getId());
        assertEquals(36, result.getId().length());
        assertEquals("Super Saiyan Son Goku", result.getName());
        assertEquals(0, result.getChildList().size());
        assertTrue(StringUtils.isBlank(result.getParent().getId()));
        assertEquals(0, result.getLevel());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(1, result.getDescendantCount());
        assertEquals(1, result.getChildCount());
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel()
                    .setName("Super Saiyan Son Goku")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
        var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue()).setParent(parentOrganize);
        this.organizeUtil.create(childOrganizeModel);
        this.organizeId = parentOrganize.getId();
    }

}
