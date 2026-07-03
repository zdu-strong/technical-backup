package com.john.project.test.common.OrganizeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.john.project.enums.OrganizeTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeUtilCreateTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeUtil.create(
                new OrganizeModel().setName("Son Gohan")
                        .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue())
                        .setParent(new OrganizeModel().setId(organizeId)));
        var result = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.organizeId);
        assertEquals(1, result.getTotalRecords());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku")
                .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
        this.organizeId = this.organizeUtil.create(organizeModel).getId();

    }
}
