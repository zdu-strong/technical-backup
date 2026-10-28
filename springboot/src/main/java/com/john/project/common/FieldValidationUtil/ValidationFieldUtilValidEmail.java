package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import com.john.project.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public abstract class ValidationFieldUtilValidEmail extends ValidationFieldUtilPassed {

    public void checkValidOfUserByEmail(UserModel userModel) {
        for (var userEmail : userModel.getUserEmailList()) {
            this.checkNotBlankOfEmail(userEmail.getEmail());
            this.checkCorrectFormatOfEmail(userEmail.getEmail());

            if (StringUtils.isNotBlank(userModel.getId())) {
                var user = this.userService.getUserWithMoreInformation(userModel.getId());
                if (user.getUserEmailList().stream().anyMatch(s -> ObjectUtil.equals(s.getEmail(), userEmail.getEmail()))) {
                    continue;
                }
            }

            if (StringUtils.isBlank(userEmail.getVerificationCodeEmail().getVerificationCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        StrFormatter.format("The verification code of email {} cannot be empty", userEmail.getEmail()));
            }

            userEmail.getVerificationCodeEmail().setEmail(userEmail.getEmail());

            this.checkNotUsedOfVerificationCodeEmail(userEmail.getVerificationCodeEmail());

            this.checkPassedOfVerificationCodeEmail(userEmail.getVerificationCodeEmail());

            this.checkNotUsedOfEmail(userEmail.getEmail());
        }
    }

}
