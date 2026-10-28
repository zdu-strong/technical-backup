package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.UserMessageEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilCanRecall extends ValidationFieldUtilCanDelete {

    @Transactional(readOnly = true)
    public void checkCanRecallOfUserMessage(HttpServletRequest request, String id) {
        var userMessageEntity = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (!userMessageEntity.getUser().getId().equals(this.permissionUtil.getUserId(request))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recall your own messages");
        }
        if (userMessageEntity.getIsRecall()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot recall again");
        }
    }

}
