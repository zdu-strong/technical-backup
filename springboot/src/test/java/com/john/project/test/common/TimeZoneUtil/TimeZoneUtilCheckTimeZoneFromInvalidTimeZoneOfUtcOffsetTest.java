package com.john.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilCheckTimeZoneFromInvalidTimeZoneOfUtcOffsetTest extends BaseTest {

    private String timeZone;

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.timeZoneUtil.checkTimeZone(timeZone);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.timeZone = "+08:00";
    }

}
