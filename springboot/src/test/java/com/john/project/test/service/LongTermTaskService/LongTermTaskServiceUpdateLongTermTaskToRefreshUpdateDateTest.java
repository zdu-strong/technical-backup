package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.john.project.model.LongTermTaskModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskToRefreshUpdateDateTest extends BaseTest {

    private String longTermTaskId;
    private Date updateDate;

    @Test
    public void test() {
        this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(this.longTermTaskId);
        var result = (LongTermTaskModel<?>) this.longTermTaskService
                .getLongTermTask(this.longTermTaskId).getBody();
        assertTrue(result.getUpdateDate().after(this.updateDate));
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermTaskId = this.longTermTaskService.createLongTermTask();
        this.updateDate = ((LongTermTaskModel<?>) this.longTermTaskService.getLongTermTask(this.longTermTaskId)
                .getBody()).getUpdateDate();
        ThreadUtils.sleepQuietly(Duration.ofMillis(1));
    }

}
