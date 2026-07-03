package com.john.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.*;

import com.john.project.enums.OrganizeTypeEnum;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import com.john.project.model.OrganizeModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerMoveOrganizeTest extends BaseTest {

    private String organizeId;
    private String parentOrganizeId;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/organize/move").setParameter("id", organizeId)
                .setParameter("parentId", parentOrganizeId)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
        var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.createAccount(email);
        {
            var parentOrganizeModel = new OrganizeModel()
                    .setName("Super Saiyan Son Goku")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel()
                    .setName("Son Gohan")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue())
                    .setParent(parentOrganize);
            var childOrganize = this.organizeUtil.create(childOrganizeModel);
            this.organizeId = childOrganize.getId();
        }
        {
            var parentOrganizeModel = new OrganizeModel()
                    .setName("Piccolo")
                    .setOrganizeType(OrganizeTypeEnum.ORGANIZE.getValue());
            var parentOrganize = this.organizeUtil.create(parentOrganizeModel);
            this.parentOrganizeId = parentOrganize.getId();
        }
    }

}
