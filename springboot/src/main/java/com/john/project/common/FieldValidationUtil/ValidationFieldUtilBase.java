package com.john.project.common.FieldValidationUtil;

import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.storage.Storage;
import com.john.project.service.StorageSpaceService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ValidationFieldUtilBase {

    @Autowired
    protected Storage storage;

    @Autowired
    protected StorageSpaceService storageSpaceService;

    @Autowired
    protected LongTermTaskUtil longTermTaskUtil;

}
