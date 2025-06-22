package com.john.project.scheduled;

import java.util.List;
import java.util.concurrent.Executor;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import com.john.project.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.john.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Getter;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private GitProperties gitProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    private VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    private LongTermTaskService longTermTaskService;

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @Autowired
    private RoleService roleService;

    @Getter
    private Boolean hasInit = false;

    @Autowired
    private Executor applicationTaskExecutor;

    @Scheduled(initialDelay = 0, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        synchronized (this) {
            if (hasInit) {
                return;
            }
            this.longTermTaskUtil.runSkipWhenExists(() -> {
                this.init();
            }, getLongTermTaskUniqueKeyModelForInitSystemData());
            this.distributedExecutionUtil.initializeInSystemInitScheduled();
            this.hasInit = true;
        }
    }

    private void init() {
        this.initEncryptDecryptKey();
        this.initUserRole();
        this.initSuperAdminUser();
    }

    private void initSuperAdminUser() {
        var email = "zdu.strong@gmail.com";
        var hasExists = !Flowable.fromCallable(() -> {
                    return this.userService.getUserId(email);
                })
                .onErrorReturnItem(StringUtils.EMPTY)
                .filter(s -> StringUtils.isNotBlank(s))
                .firstElement()
                .isEmpty()
                .blockingGet();
        if (hasExists) {
            return;
        }
        var superAdminUser = new UserModel();
        superAdminUser.setUsername("SuperAdmin");
        superAdminUser.setPassword(email);
        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        verificationCodeEmailModel.setVerificationCode(
                this.verificationCodeEmailService.getById(verificationCodeEmailModel.getId()).getVerificationCode());
        superAdminUser.setUserEmailList(
                List.of(new UserEmailModel().setEmail(email).setVerificationCodeEmail(verificationCodeEmailModel)));
        var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
        superAdminRoleQueryPaginationModel.setPageNum(1L);
        superAdminRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
        superAdminUser.setRoleList(
                this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel).getItems());
        this.userService.create(superAdminUser);
    }

    private void initEncryptDecryptKey() {
        this.encryptDecryptService.init();
        this.encryptDecryptService.getKeyOfAESSecretKey();
        this.encryptDecryptService.getKeyOfRSAPrivateKey();
        this.encryptDecryptService.getKeyOfRSAPublicKey();
    }

    private void initUserRole() {
        while (true) {
            if (!this.permissionService.refresh()) {
                break;
            }
        }
        while (true) {
            if (!this.userRoleRelationService.refresh()) {
                break;
            }
        }
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelForInitSystemData() {
        var uniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.INIT_SYSTEM_DATABASE_DATA.getValue())
                .setUniqueKey(gitProperties.getCommitId());
        return uniqueKeyModel;
    }

}
