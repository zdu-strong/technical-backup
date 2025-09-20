package com.john.project.common.FieldValidationUtil;

import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public abstract class ValidationFieldUtilValidUrl extends ValidationFieldUtilCorrectFormat {

    public void checkValidOfUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        var folderName = this.storage.storageUrl(url).getFolderName();
        this.storageSpaceService.update(folderName);
        if (this.storageSpaceService.isUsedByProgramData(folderName)) {
            return;
        }
        var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.REFRESH_STORAGE_SPACE.getValue())
                .setUniqueKey(folderName);
        var hasValid = new AtomicBoolean(false);
        this.longTermTaskUtil.runSkipAfterRetryWhenExists(() -> {
            hasValid.set(this.storageSpaceService.isUsedByTempFile(folderName));
        }, longTermTaskUniqueKeyModel);

        if (!hasValid.get()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file is invalid");
        }
    }

}
