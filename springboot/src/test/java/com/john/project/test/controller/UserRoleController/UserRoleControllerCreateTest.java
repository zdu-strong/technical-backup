package com.john.project.test.controller.UserRoleController;

import static org.junit.jupiter.api.Assertions.*;

import com.john.project.model.OrganizeModel;
import com.john.project.model.PermissionRelationModel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
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
        body.setPermissionList(JinqStream.from(user.getRoleList())
                .selectAllList(s -> s.getPermissionList())
                .group(s -> s.getOrganize().getId(), (s, t) -> t.findFirst().get())
                .select(s -> s.getOne())
                .where(StringUtils::isNotBlank)
                .select(s -> new PermissionRelationModel().setPermission(SystemPermissionEnum.ORGANIZE_MANAGE.getValue()).setOrganize(new OrganizeModel().setId(s)))
                .toList());
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
