package com.john.project.common.permission;

import com.john.project.enums.SystemPermissionEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Component
public abstract class PermissionUtilGetOrganizeIdList extends PermissionUtilGetUserId {

    public List<String> getOrganizeIdListByAnyPermission(HttpServletRequest request,
                                                         SystemPermissionEnum... permissionList) {
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(permissionList).anyMatch(s -> !s.getIsOrganizePermission())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only organize role can be transferred");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var organizeIdList = JinqStream.from(convertToPermissionList(user))
                .where(s -> {
                    var permission = SystemPermissionEnum.parse(s.getPermission());
                    return ArrayUtils.contains(permissionList, permission) && !permission.getIsSuperAdmin();
                })
                .select(s -> s.getOrganize().getId())
                .toList();
        return organizeIdList;
    }

}
