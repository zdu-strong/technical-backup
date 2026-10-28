package com.john.project.common.FieldValidationUtil;

import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.common.baseService.BaseService;
import com.john.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public abstract class ValidationFieldUtilBase extends BaseService {

    @Autowired
    @Lazy
    protected StorageSpaceService storageSpaceService;

    @Autowired
    @Lazy
    protected LongTermTaskUtil longTermTaskUtil;

    @Autowired
    @Lazy
    protected OrganizeService organizeService;

    @Autowired
    @Lazy
    protected RoleService roleService;

    @Autowired
    @Lazy
    protected UserService userService;

    @Autowired
    @Lazy
    protected VerificationCodeEmailService verificationCodeEmailService;

}
