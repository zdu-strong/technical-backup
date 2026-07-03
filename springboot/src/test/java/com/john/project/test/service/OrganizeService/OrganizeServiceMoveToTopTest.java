package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.john.project.enums.OrganizeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceMoveToTopTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeUtil.move(organizeId, null);
        var result = this.organizeService.getById(organizeId);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isBlank(result.getParent().getId()));
        assertEquals(0, result.getLevel());
    }

    @BeforeEach
    public void beforeEach() {
        var parentOrganizeModel = new OrganizeModel()
                    .setName("Super Saiyan Son Goku")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
        var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue()).setParent(parentOrganize);
        var childOrganize = this.organizeUtil.create(childOrganizeModel);
        this.organizeId = childOrganize.getId();
    }

}
