package com.john.project.test.controller.SuperAdminRoleQueryController;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.OrganizeModel;
import com.john.project.model.PaginationModel;
import com.john.project.model.RoleModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuperAdminRoleQueryControllerSearchByPaginationByPermissionListTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/super-admin/role/search/pagination")
                .setParameter("pageNum", "1")
                .setParameter("pageSize", "200")
                .addParameter("permissionList", SystemPermissionEnum.SUPER_ADMIN.getValue())
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PaginationModel<RoleModel>>() {
        });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var roleList = response.getBody().getItems();
        assertEquals(roleList.size(),
                JinqStream.from(roleList)
                        .selectAllList(s -> s.getPermissionList())
                        .where(s -> ObjectUtil.equals(s.getPermission(), SystemRoleEnum.SUPER_ADMIN.getValue()))
                        .select(s -> s.getRole().getId())
                        .distinct()
                        .count());
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
