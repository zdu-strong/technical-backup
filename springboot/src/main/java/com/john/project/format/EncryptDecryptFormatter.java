package com.john.project.format;

import cn.hutool.core.text.StrFormatter;
import com.john.project.common.baseService.BaseService;
import com.john.project.common.database.JPQLFunction;
import com.john.project.entity.EncryptDecryptEntity;
import com.john.project.model.EncryptDecryptModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.john.project.constant.EncryptDecryptConstant.ROUTINE_ROTATION;

@Service
public class EncryptDecryptFormatter extends BaseService {

    public EncryptDecryptModel format(EncryptDecryptEntity encryptDecryptEntity) {
        var encryptDecryptModel = new EncryptDecryptModel();
        BeanUtils.copyProperties(encryptDecryptEntity, encryptDecryptModel);
        if (encryptDecryptModel.getName().startsWith(ROUTINE_ROTATION.substring(0, ROUTINE_ROTATION.length() - 2))) {
            try {
                var name = StrFormatter.format(ROUTINE_ROTATION,
                        JPQLFunction.formatDateAsYearMonthDay(encryptDecryptModel.getCreateDate(), "UTC"));
                encryptDecryptModel.setName(name);
            } catch (Throwable e) {
                // do nothing
            }
        }
        return encryptDecryptModel;
    }

}
