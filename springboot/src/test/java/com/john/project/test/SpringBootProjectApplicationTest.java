package com.john.project.test;

import static org.junit.jupiter.api.Assertions.*;

import com.john.project.SpringBootProjectApplication;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class SpringBootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(SpringBootProjectApplication.class, "Startup class does not exist");
    }

}
