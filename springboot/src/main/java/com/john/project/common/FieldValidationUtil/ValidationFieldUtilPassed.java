package com.john.project.common.FieldValidationUtil;

import com.john.project.entity.VerificationCodeEmailEntity;
import com.john.project.model.VerificationCodeEmailModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilPassed extends ValidationFieldUtilNotUsed {

    public void checkPassedOfVerificationCodeEmail(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();
        var verificationCodeEmailEntity = this.streamAll(VerificationCodeEmailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!verificationCodeEmailEntity.getVerificationCode()
                .equals(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        verificationCodeEmailEntity.setIsPassed(true);
        this.merge(verificationCodeEmailEntity);
    }

}
