package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.UserEmailEntity;
import com.john.project.entity.VerificationCodeEmailEntity;
import com.john.project.model.VerificationCodeEmailModel;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public abstract class ValidationFieldUtilNotUsed extends ValidationFieldUtilValidEnumValue {

    @Transactional(readOnly = true)
    public void checkNotUsedOfEmail(String email) {
        var isPresent = this.streamAll(UserEmailEntity.class)
                .where(s -> s.getEmail().equals(email))
                .where(s -> !s.getIsDeleted())
                .where(s -> !s.getUser().getIsDeleted())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }

    public void checkNotUsedOfVerificationCodeEmail(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();

        var verificationCodeEmailEntityOptional = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .findOne();
        if (!verificationCodeEmailEntityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        var verificationCodeEmailEntity = verificationCodeEmailEntityOptional.get();

        if (!this.verificationCodeEmailService.isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailEntity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (verificationCodeEmailEntity.getHasUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (!verificationCodeEmailEntity.getEmail().equals(verificationCodeEmailModel.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        {
            var expiredDate = DateUtils.addMinutes(new Date(), 5);

            if (!verificationCodeEmailEntity.getCreateDate().before(expiredDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
            }

        }

        verificationCodeEmailEntity.setHasUsed(true);
        this.merge(verificationCodeEmailEntity);
    }

}
