package com.john.project.test.service.OrganizeRelationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeRelationServiceRefreshTest extends BaseTest {

    private String parentOrganizeId;
    private String childOrganizeId;

    @Test
    public void test() {
        while (true) {
            var hasNext = this.organizeRelationService.refresh(this.childOrganizeId);
            if (!hasNext) {
                break;
            }
        }
        var result = this.organizeService.searchByName(1L, 20L, "Gohan", this.parentOrganizeId);
        assertEquals(1, result.getTotalRecords());
        assertEquals(this.childOrganizeId, JinqStream.from(result.getItems()).select(s -> s.getId()).getOnlyValue());
        assertEquals(this.parentOrganizeId,
                JinqStream.from(result.getItems()).select(s -> s.getParent().getId()).getOnlyValue());
        assertEquals("Son Gohan",
                JinqStream.from(result.getItems()).select(s -> s.getName()).getOnlyValue());
        assertEquals(1,
                JinqStream.from(result.getItems()).select(s -> s.getLevel()).getOnlyValue());
        assertEquals(0,
                JinqStream.from(result.getItems()).select(s -> s.getChildCount()).getOnlyValue());
        assertEquals(0,
                JinqStream.from(result.getItems()).select(s -> s.getDescendantCount()).getOnlyValue());
        assertEquals(0,
                JinqStream.from(result.getItems()).select(s -> s.getChildList().size()).getOnlyValue());
        assertNotNull(
                JinqStream.from(result.getItems()).select(s -> s.getCreateDate()).getOnlyValue());
        assertNotNull(
                JinqStream.from(result.getItems()).select(s -> s.getUpdateDate()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
            var childOrganize = this.organizeUtil.create(childOrganizeModel);
            this.childOrganizeId = childOrganize.getId();
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            this.parentOrganizeId = parentOrganize.getId();
        }
        this.organizeUtil.move(this.childOrganizeId, this.parentOrganizeId);
        var pagination = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.parentOrganizeId);
        assertEquals(1, pagination.getTotalRecords());
    }

}
