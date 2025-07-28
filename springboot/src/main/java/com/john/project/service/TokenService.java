package com.john.project.service;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Base64;
import java.util.Date;

import com.john.project.entity.TokenEntity;
import com.john.project.entity.UserEntity;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.TokenModel;
import cn.hutool.crypto.CryptoException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService extends BaseService {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    public String generateAccessToken(String userId, String encryptedPassword) {
        this.checkCorrectPassword(userId, encryptedPassword);

        var tokenModel = this.createTokenEntity(userId, encryptedPassword);
        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(tokenModel.getId())
                .sign(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()));
        return accessToken;
    }

    @Transactional(readOnly = true)
    public String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

    @Transactional(readOnly = true)
    public DecodedJWT getDecodedJWTOfAccessToken(String accessToken) {
        var decodedJWT = JWT
                .require(Algorithm.RSA512(this.encryptDecryptService.getKeyOfRSAPublicKey(),
                        this.encryptDecryptService.getKeyOfRSAPrivateKey()))
                .build().verify(accessToken);
        if (!this.hasExistTokenEntity(decodedJWT.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first and then visit");
        }
        return decodedJWT;
    }

    @Transactional(readOnly = true)
    public boolean hasExistTokenEntity(String id) {
        var exists = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .exists();
        return exists;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String getEncryptedPassword(String password) {
        var encryptedPassword = this.encryptDecryptService.encryptByPublicKeyOfRSA(objectMapper.writeValueAsString(new Object[]{DigestUtils.sha3_512Hex(password), new Date()}));
        return encryptedPassword;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String getDecryptedPassword(String encryptedPassword) {
        var passwordJsonString = this.encryptDecryptService.decryptByByPrivateKeyOfRSA(encryptedPassword);
        var password = this.objectMapper.readTree(passwordJsonString).get(0).asText();
        return password;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public Date getCreateDateOfEncryptedPassword(String encryptedPassword) {
        var passwordJsonString = this.encryptDecryptService.decryptByByPrivateKeyOfRSA(encryptedPassword);
        var createDate = this.objectMapper.treeToValue(this.objectMapper.readTree(passwordJsonString).get(1), Date.class);
        return createDate;
    }

    @SneakyThrows
    public String getPasswordInDatabaseOfEncryptedPassword(String encryptedPassword, String userId) {
        var password = this.getDecryptedPassword(encryptedPassword);
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
        var passwordAfterEncrypted = this.encryptDecryptService.encryptByAES(objectMapper.writeValueAsString(new Object[]{userId, this.uuidUtil.v4()}), secretKeyOfAES);
        return passwordAfterEncrypted;
    }

    public void deleteTokenEntity(String id) {
        var tokenEntity = this.streamAll(TokenEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        tokenEntity.setIsDeleted(true);
        this.merge(tokenEntity);
    }

    private String getUniqueOneTimePasswordLogo(String encryptedPassword) {
        var logo = Base64.getEncoder().encodeToString(DigestUtils.sha3_512(encryptedPassword));
        return logo;
    }

    @SneakyThrows
    private void checkCorrectPassword(String userId, String encryptedPassword) {
        try {
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .getOnlyValue();
            var createDate = this.getCreateDateOfEncryptedPassword(encryptedPassword);
            if (createDate.before(DateUtils.addMinutes(new Date(), -5))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            if (createDate.after(DateUtils.addMinutes(new Date(), 5))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
            var password = this.getDecryptedPassword(encryptedPassword);
            var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES(DigestUtils.sha3_512Hex(userId + password));
            var passwordJsonString = this.encryptDecryptService.decryptByAES(userEntity.getPassword(), secretKeyOfAES);
            var userIdOfPasswordInDatabase = this.objectMapper.readTree(passwordJsonString).get(0).asText();
            if (!userId.equals(userIdOfPasswordInDatabase)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }

            var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(encryptedPassword);
            var exists = this.streamAll(TokenEntity.class)
                    .where(s -> s.getUser().getId().equals(userId))
                    .where(s -> s.getUniqueOneTimePasswordLogo().equals(uniqueOneTimePasswordLogo))
                    .exists();
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Incorrect username or password");
            }
        } catch (UndeclaredThrowableException | CryptoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Incorrect username or password");
        }
    }

    private TokenModel createTokenEntity(String userId, String encryptedPassword) {
        var uniqueOneTimePasswordLogo = this.getUniqueOneTimePasswordLogo(encryptedPassword);
        var user = this.streamAll(UserEntity.class).where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(newId());
        tokenEntity.setUniqueOneTimePasswordLogo(uniqueOneTimePasswordLogo);
        tokenEntity.setUser(user);
        tokenEntity.setIsDeleted(false);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);

        return this.tokenFormatter.format(tokenEntity);
    }

}
