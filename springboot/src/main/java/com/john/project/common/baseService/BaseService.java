package com.john.project.common.baseService;

import com.google.cloud.spanner.AbortedDueToConcurrentModificationException;
import com.john.project.common.FieldValidationUtil.ValidationFieldUtil;
import com.john.project.common.uuid.UUIDUtil;
import com.john.project.format.*;
import io.grpc.StatusRuntimeException;
import org.hibernate.exception.GenericJDBCException;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spanner.AbortedException;
import com.john.project.common.TimeZoneUtil.TimeZoneUtil;
import com.john.project.common.database.JPQLFunction;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.properties.DatabaseJdbcProperties;
import com.john.project.properties.DevelopmentMockModeProperties;
import com.john.project.common.storage.Storage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional(rollbackFor = Throwable.class)
@Retryable(maxAttempts = 10, retryFor = {
        GenericJDBCException.class,
        ObjectOptimisticLockingFailureException.class,
        AbortedException.class,
        CannotAcquireLockException.class,
        CannotCreateTransactionException.class,
        DataAccessResourceFailureException.class,
        IllegalStateException.class,
        StatusRuntimeException.class,
        AbortedDueToConcurrentModificationException.class,
})
public abstract class BaseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected Storage storage;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    protected UUIDUtil uuidUtil;

    @Autowired
    protected ValidationFieldUtil validationFieldUtil;

    @Autowired
    private DatabaseJdbcProperties databaseJdbcProperties;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @Autowired
    protected TokenFormatter tokenFormatter;

    @Autowired
    protected StorageSpaceFormatter storageSpaceFormatter;

    @Autowired
    protected UserEmailFormatter userEmailFormatter;

    @Autowired
    protected UserFormatter userFormatter;

    @Autowired
    protected LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    protected OrganizeFormatter organizeFormatter;

    @Autowired
    protected UserMessageFormatter userMessageFormatter;

    @Autowired
    protected FriendshipFormatter friendshipFormatter;

    @Autowired
    protected LoggerFormatter loggerFormatter;

    @Autowired
    protected NonceFormatter nonceFormatter;

    @Autowired
    protected VerificationCodeEmailFormatter verificationCodeEmailFormatter;

    @Autowired
    protected DistributedExecutionMainFormatter distributedExecutionMainFormatter;

    @Autowired
    protected RoleFormatter roleFormatter;

    @Autowired
    protected DistributedExecutionDetailFormatter distributedExecutionDetailFormatter;

    @Autowired
    protected PermissionRelationFormatter permissionRelationFormatter;

    protected void persist(Object entity) {
        this.entityManager.persist(entity);
    }

    protected void merge(Object entity) {
        this.entityManager.merge(entity);
    }

    protected void remove(Object entity) {
        this.entityManager.remove(entity);
    }

    protected <U> JPAJinqStream<U> streamAll(Class<U> entity) {
        var jinqJPAStreamProvider = new JinqJPAStreamProvider(
                entityManager.getMetamodel());
        JPQLFunction.registerCustomSqlFunction(jinqJPAStreamProvider);
        jinqJPAStreamProvider.setHint("exceptionOnTranslationFail", true);
        return jinqJPAStreamProvider.streamAll(entityManager, entity);
    }

    protected String newId() {
        if (this.databaseJdbcProperties.getIsNewSqlDatabase()) {
            return this.uuidUtil.v4();
        } else {
            return this.uuidUtil.v7();
        }
    }

}