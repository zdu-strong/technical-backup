package com.john.project.common.permission;

import com.john.project.enums.SystemPermissionEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Component
public abstract class PermissionUtilIsHasAnyPermission extends PermissionUtilIsSign {

    public boolean hasAnyPermission(HttpServletRequest request, SystemPermissionEnum... permissionList) {
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        var hasAnyPermission = JinqStream.from(convertToPermissionList(user))
                .select(s -> SystemPermissionEnum.parse(s.getPermission()))
                .where(s -> ArrayUtils.contains(permissionList, s))
                .exists();
        return hasAnyPermission;
    }

    public boolean hasAnyPermission(HttpServletRequest request, String organizeId,
                                    SystemPermissionEnum... permissionList) {
        if (StringUtils.isBlank(organizeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganizeId Cannot be empty");
        }
        if (permissionList.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList cannot be empty");
        }
        if (Arrays.stream(permissionList).anyMatch(s -> !s.getIsOrganizePermission() && !s.getIsSuperAdmin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a superAdmin and not an organize role");
        }
        var userId = this.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);

        if (Arrays.stream(permissionList).anyMatch(s -> s.getIsSuperAdmin())
                && JinqStream.from(convertToPermissionList(user))
                .select(s -> SystemPermissionEnum.parse(s.getPermission()))
                .anyMatch(s -> s.getIsSuperAdmin())) {
            return true;
        }
        var organizeIdList = this.getOrganizeIdListByAnyPermission(request, permissionList);
        var ancestorIdList = this.organizeService.getAccestorIdList(organizeId);
        if (JinqStream.from(ancestorIdList).anyMatch(s -> organizeIdList.contains(s))) {
            return true;
        }

        return false;
    }

}
