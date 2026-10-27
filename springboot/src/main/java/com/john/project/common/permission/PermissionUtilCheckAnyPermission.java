package com.john.project.common.permission;

import com.john.project.enums.SystemPermissionEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public abstract class PermissionUtilCheckAnyPermission extends PermissionUtilIsHasAnyPermission {

    public void checkAnyPermission(HttpServletRequest request, SystemPermissionEnum... permissionList) {
        if (!this.hasAnyPermission(request, permissionList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public void checkAnyPermission(HttpServletRequest request, String organizeId,
                                   SystemPermissionEnum... permissionList) {
        if (StringUtils.isBlank(organizeId)) {
            this.checkAnyPermission(request, permissionList);
        } else if (!this.hasAnyPermission(request, organizeId, permissionList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public void checkAnyPermissionByDownstreamMember(HttpServletRequest request, String organizeId,
                                               SystemPermissionEnum... permissionList) {
        if (!this.hasAnyPermissionByDownstreamMember(request, organizeId, permissionList)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
