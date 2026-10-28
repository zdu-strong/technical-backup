package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.UserMessageEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public abstract class ValidationFieldUtilCanDelete extends ValidationFieldUtilCanUpdate {

    @Transactional(readOnly = true)
    public void checkCanDeleteOfRole(HttpServletRequest request, String id) {
        this.checkPermissionRelation(request, this.roleService.getRoleById(id).setPermissionList(List.of()));
    }

    @Transactional(readOnly = true)
    public void checkCanDeleteOfUser(HttpServletRequest request, String userId) {
        this.checkPermissionRelation(request, this.userService.getUser(userId).setRoleList(List.of()).setIdentityCardList(List.of()));
    }

    @Transactional(readOnly = true)
    public void checkCanDeleteOfUserMessage(HttpServletRequest request, String id) {
        var userId = this.permissionUtil.getUserId(request);
        var exists = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete again");
        }
    }

}
