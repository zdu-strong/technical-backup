package com.john.project.common.permission;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.IdentityCardModel;
import com.john.project.model.PermissionRelationModel;
import com.john.project.model.UserModel;
import com.john.project.service.OrganizeService;
import com.john.project.service.TokenService;
import com.john.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public abstract class PermissionUtilBase {

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected UserService userService;

    protected String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

    public List<PermissionRelationModel> convertToPermissionList(UserModel user) {
        var identityCardList = Optional.ofNullable(user)
                .map(s -> s.getIdentityCardList())
                .orElse(new ArrayList<>());
        var permissionList = JinqStream.from(List.of(
                        JinqStream.from(identityCardList)
                                .select(s -> {
                                    var permissionRelationModel = new PermissionRelationModel();
                                    permissionRelationModel.setPermission(s.getIdentityType());
                                    permissionRelationModel.setOrganize(s.getGovernanceRegion());
                                    return permissionRelationModel;
                                })
                                .toList(),
                        JinqStream.from(user.getRoleList())
                                .selectAllList(s -> s.getPermissionList())
                                .toList()
                ))
                .selectAllList(s -> s)
                .where(s -> ObjectUtil.isNotNull(EnumUtil.getBy(SystemPermissionEnum::getValue, s.getPermission())))
                .toList();
        return permissionList;
    }

    protected List<IdentityCardModel> convertToIdentityCardlList(UserModel user) {
        var identityCardList = Optional.ofNullable(user)
                .map(s -> s.getIdentityCardList())
                .orElse(new ArrayList<>());
        return JinqStream.from(identityCardList)
                .where(s -> ObjectUtil.isNotNull(EnumUtil.getBy(SystemPermissionEnum::getValue, s.getIdentityType())))
                .toList();
    }

}
