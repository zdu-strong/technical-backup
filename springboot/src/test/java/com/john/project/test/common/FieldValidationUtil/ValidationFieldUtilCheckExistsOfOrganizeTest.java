package com.john.project.test.common.FieldValidationUtil;

import com.john.project.enums.OrganizeTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class ValidationFieldUtilCheckExistsOfOrganizeTest extends BaseTest {
    private String organizeId;

    @Test
    public void test() {
        this.validationFieldUtil.checkExistsOfOrganize(this.organizeId);
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel()
                .setName("Super Saiyan Son Goku")
                .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
        this.organizeId = this.organizeUtil.create(organizeModel).getId();
    }

}
