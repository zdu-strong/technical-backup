package com.john.project.service;

import java.util.Date;

import cn.hutool.core.text.StrFormatter;
import com.john.project.common.database.JPQLFunction;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.EncryptDecryptEntity;

import static com.john.project.constant.EncryptDecryptConstant.*;

@Service
public class EncryptDecryptService extends BaseService {

    public void createEmergencyRotationSecretKey() {
        var name = StrFormatter.format(EMERGENCY_ROTATION, JPQLFunction.formatDateAsYearMonthDayHourMinuteSecondMillisecond(new Date(), "UTC"));
        this.create(name);
    }

    public void delete(String id) {
        var encryptDecryptEntity = this.streamAll(EncryptDecryptEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(encryptDecryptEntity);
    }

    public boolean init() {
        var expiredDate = DateUtils.addDays(new Date(), -(int) SECRET_KEY_EXPIRED_DURATION.toDays());
        var validDate = DateUtils.addDays(new Date(), -(int) SECRET_KEY_ROTATION_DURATION.toDays());

        if (!isValid()
                && !this.streamAll(EncryptDecryptEntity.class)
                .where(s -> !s.getCreateDate().before(validDate))
                .exists()
        ) {
            if (!this.deleteExpired()) {
                var name = this.getName();
                this.create(name);
            }
        } else {
            // Query valid secret keys
            var encryptDecryptModelList = this.streamAll(EncryptDecryptEntity.class)
                    .where(s -> !s.getCreateDate().before(expiredDate))
                    .map(this.encryptDecryptFormatter::format)
                    .toList();
            if (JinqStream.from(encryptDecryptModelList)
                    .where(s -> !s.getCreateDate().before(validDate))
                    .exists()) {
                this.encryptDecryptUtil.setEncryptDecryptModelList(encryptDecryptModelList);
            }
        }

        return isValid();
    }

    private void create(String name) {
        var encryptDecryptEntity = new EncryptDecryptEntity();
        encryptDecryptEntity.setId(newId());
        encryptDecryptEntity.setName(name);
        encryptDecryptEntity.setCreateDate(new Date());
        encryptDecryptEntity.setUpdateDate(new Date());

        /*
         * aes for common uses
         */
        encryptDecryptEntity.setSecretKeyOfAES(this.encryptDecryptUtil.generateSecretKeyOfAES());

        /*
         * ml-kem for common uses
         */
        var keyPairOfMLKEM = this.encryptDecryptUtil.generateKeyPairOfMLKEM();
        encryptDecryptEntity.setPublicKeyOfMLKEM(keyPairOfMLKEM.getPublicKeyOfMLKEM());
        encryptDecryptEntity.setPrivateKeyOfMLKEM(keyPairOfMLKEM.getPrivateKeyOfMLKEM());

        this.persist(encryptDecryptEntity);
    }

    private boolean deleteExpired() {
        var expiredDate = DateUtils.addDays(new Date(), -(int) SECRET_KEY_EXPIRED_DURATION.toDays());
        var encryptDecryptEntityList = this.streamAll(EncryptDecryptEntity.class)
                .where(s -> s.getCreateDate().before(expiredDate))
                .toList();
        for (var encryptDecryptEntity : encryptDecryptEntityList) {
            this.remove(encryptDecryptEntity);
            return true;
        }
        return false;
    }

    private boolean isValid() {
        var isValid = JinqStream.from(this.encryptDecryptUtil.getEncryptDecryptModelList())
                .where(s -> !s.getCreateDate().before(DateUtils.addDays(new Date(), -(int) SECRET_KEY_ROTATION_DURATION.toDays())))
                .exists();
        return isValid;
    }

    private String getName() {
        var expiredDate = DateUtils.addDays(new Date(), -(int) SECRET_KEY_EXPIRED_DURATION.toDays());
        var name = this.streamAll(EncryptDecryptEntity.class)
                .where(s -> !s.getCreateDate().before(expiredDate))
                .sortedDescendingBy(s -> s.getCreateDate())
                .limit(1)
                .findOne()
                .map(s -> s.getCreateDate())
                .map(s -> DateUtils.addDays(s, (int) SECRET_KEY_ROTATION_DURATION.toDays()))
                .map(s -> JPQLFunction.formatDateAsYearMonthDay(s, "UTC"))
                .map(s -> StrFormatter.format(ROUTINE_ROTATION, s))
                .orElse(StrFormatter.format(ROUTINE_ROTATION, "INITIALIZE_SECRET_KEY"));
        return name;
    }

}
