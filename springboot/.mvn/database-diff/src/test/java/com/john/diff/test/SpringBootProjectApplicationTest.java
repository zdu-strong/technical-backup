package com.john.diff.test;

import com.john.diff.SpringBootProjectApplication;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import com.john.diff.test.common.BaseTest.BaseTest;

public class SpringBootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        SpringBootProjectApplication.main(ArrayUtils.EMPTY_STRING_ARRAY);
    }

}
