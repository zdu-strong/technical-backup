package com.john.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceCreateOrganizeTest extends BaseTest {

    @Test
    public void test() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var result = this.organizeUtil.create(organizeModel);
        assertNotNull(result.getId());
        assertEquals(36, result.getId().length());
        assertEquals("Super Saiyan Son Goku", result.getName());
        assertEquals(0, result.getChildList().size());
        assertEquals(0, result.getChildCount());
        assertEquals(0, result.getDescendantCount());
        assertTrue(StringUtils.isBlank(result.getParent().getId()));
        assertEquals(0, result.getLevel());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
    }

}
