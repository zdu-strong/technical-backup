package com.john.project.test.controller.SuperAdminUserQueryController;

import com.john.project.model.OrganizeModel;
import com.john.project.model.PaginationModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SuperAdminUserQueryControllerTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/super-admin/user/search/pagination")
                .setParameter("pageNum", "1")
                .setParameter("pageSize", "200")
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<PaginationModel<UserModel>>() {
        });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getPageNum());
        assertEquals(200, response.getBody().getPageSize());
        assertEquals(2, response.getBody().getTotalRecords());
        assertEquals(1, response.getBody().getTotalPages());
        assertFalse(response.getBody().getItems().isEmpty());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
            this.createAccount(email);
            var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            this.organizeUtil.create(organizeModel);
        }
        {
            var email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
            this.createAccountOfSuperAdmin(email);
        }
    }
}
