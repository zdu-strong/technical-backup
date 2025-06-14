package com.john.project.test.controller.UserRoleController;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.UserModel;
import com.john.project.model.RoleModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserRoleControllerCreateTest extends BaseTest {

    private UserModel user;

    @Test
    @SneakyThrows
    public void test() {
        var body = new RoleModel();
        body.setName("Manager");
        body.setOrganizeList(JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getOrganizeList())
                .group(s -> s.getId(), (s, t) -> t.findFirst().get())
                .select(s -> s.getTwo())
                .toList());
        body.setPermissionList(List.of(SystemPermissionEnum.ORGANIZE_MANAGE.getValue()));
        var url = new URIBuilder("/role/create").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(body), RoleModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() {
        var email = this.uuidUtil.v4() + "@gmail.com";
        this.user = this.createAccountOfCompanyAdmin(email);
    }

}
