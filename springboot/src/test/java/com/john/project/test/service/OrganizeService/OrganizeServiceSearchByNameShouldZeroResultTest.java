package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceSearchByNameShouldZeroResultTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var result = this.organizeService.searchByName(1L, 20L, "super Saiyan Son Goku", organizeId);
        assertEquals(0, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(1, result.getPageNum());
        assertEquals(20, result.getPageSize());
        assertEquals(0, result.getItems().size());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeUtil.create(organizeModel);
        this.organizeId = parentOrganize.getId();
    }

}
