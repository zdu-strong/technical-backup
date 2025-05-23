package com.john.project.test.properties.ServerAddressProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class ServerAddressPropertiesGetServerAddressTest extends BaseTest {

    @Test
    public void test() {
        var serverAddress = this.serverAddressProperties.getServerAddress();
        assertTrue(StringUtils.isNotBlank(serverAddress));
        assertTrue(serverAddress.startsWith("http://127.0.0.1:"));
    }

}
