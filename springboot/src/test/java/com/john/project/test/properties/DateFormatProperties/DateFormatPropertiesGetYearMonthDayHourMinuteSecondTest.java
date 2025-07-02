package com.john.project.test.properties.DateFormatProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.john.project.constant.DateFormatConstant;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class DateFormatPropertiesGetYearMonthDayHourMinuteSecondTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("yyyy-MM-dd HH:mm:ss", DateFormatConstant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
    }

}
