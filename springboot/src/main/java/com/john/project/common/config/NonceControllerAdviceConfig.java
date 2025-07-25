package com.john.project.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import com.john.project.constant.DateFormatConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.project.constant.NonceConstant;
import com.john.project.service.NonceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

@Configuration
@RestControllerAdvice
public class NonceControllerAdviceConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NonceService nonceService;

    @Autowired
    private HttpServletRequest request;

    @ModelAttribute
    @SneakyThrows
    public void checkNonce(@RequestHeader(name = "X-Nonce", required = false) String nonce,
            @RequestHeader(name = "X-Timestamp", required = false) String timestampString) {
        if (StringUtils.isBlank(nonce)) {
            return;
        }
        if (StringUtils.isBlank(timestampString)) {
            return;
        }
        if (request.getAttribute("X-Nonce") != null) {
            return;
        } else {
            request.setAttribute("X-Nonce", nonce);
        }

        var timestamp = convertDateStringToDate(timestampString);
        if (timestamp
                .after(DateUtils.addMilliseconds(new Date(), (int) NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nonce has expired");
        }
        if (timestamp.before(
                DateUtils.addMilliseconds(new Date(), (int) -NonceConstant.NONCE_SURVIVAL_DURATION.toMillis()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nonce has expired");
        }
        nonce = Base64.getEncoder().encodeToString(
                this.objectMapper.writeValueAsString(new Pair<>(timestamp, nonce)).getBytes(StandardCharsets.UTF_8));
        if (nonce.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid nonce");
        }

        try {
            this.nonceService.create(nonce);
        } catch (DataIntegrityViolationException | JpaSystemException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate nonce detected");
        }
    }

    private Date convertDateStringToDate(String timestampString) {
        try {
            return FastDateFormat.getInstance(DateFormatConstant.UTC).parse(timestampString);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp");
        }
    }

}