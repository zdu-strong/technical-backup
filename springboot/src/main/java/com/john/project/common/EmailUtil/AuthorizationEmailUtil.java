package com.john.project.common.EmailUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

import com.john.project.constant.DateFormatConstant;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.properties.AuthorizationEmailProperties;
import com.john.project.properties.DevelopmentMockModeProperties;
import com.john.project.model.VerificationCodeEmailModel;
import com.john.project.service.VerificationCodeEmailService;

@Component
public class AuthorizationEmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    private DevelopmentMockModeProperties developmentMockModeProperties;

    @Autowired
    private VerificationCodeEmailService verificationCodeEmailService;

    @SneakyThrows
    public VerificationCodeEmailModel sendVerificationCode(String email) {
        VerificationCodeEmailModel verificationCodeEmailModel = null;
        for (var i = 10; i > 0; i--) {
            var verificationCodeEmailModelTwo = this.verificationCodeEmailService.createVerificationCodeEmail(email);

            var fastDateFormat = FastDateFormat.getInstance(DateFormatConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
            var createDate = fastDateFormat.parse(
                    fastDateFormat.format(DateUtils.addSeconds(verificationCodeEmailModelTwo.getCreateDate(), 1)));
            ThreadUtils.sleepQuietly(
                    Duration.ofMillis(createDate.getTime() - verificationCodeEmailModelTwo.getCreateDate().getTime()));

            if (this.verificationCodeEmailService
                    .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailModelTwo.getId())) {
                verificationCodeEmailModel = verificationCodeEmailModelTwo;
                break;
            }
        }

        if (verificationCodeEmailModel == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many verification code requests in a short period of time");
        }

        if (!this.developmentMockModeProperties.getIsDevelopmentMockMode()) {
            this.sendEmail(email, verificationCodeEmailModel.getVerificationCode());
            verificationCodeEmailModel.setVerificationCode(StringUtils.EMPTY);
        }

        return verificationCodeEmailModel;
    }

    @SneakyThrows
    private void sendEmail(String email, String verificationCode) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(this.authorizationEmailProperties.getSenderEmail());
        helper.setTo(email);
        helper.setSubject("Verification Code For Login");
        helper.setText(this.getEmailOfBody(verificationCode), true);
        mailSender.send(message);
    }

    @SneakyThrows
    private String getEmailOfBody(String verificationCode) {
        try (InputStream input = new ClassPathResource("email/email.xml").getInputStream()) {
            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            String content = text.replaceAll(this.getRegex("verificationCode"), verificationCode);
            return content;
        }
    }

    private String getRegex(String name) {
        String regex = Pattern.quote("${") + "\\s*" + Pattern.quote(name) + "\\s*" + Pattern.quote("}");
        return regex;
    }

}
