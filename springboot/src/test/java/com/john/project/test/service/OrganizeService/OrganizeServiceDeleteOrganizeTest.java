package com.john.project.test.service.OrganizeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceDeleteOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        this.organizeUtil.delete(this.organizeId);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeUtil.create(organizeModel).getId();
    }

}
