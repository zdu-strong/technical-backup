package com.john.project.test.properties.ServerAddressProperties;

import com.john.project.test.common.BaseTest.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerAddressPropertiesGetWebSocketServerAddressTest extends BaseTest {

    @Test
    public void test() {
        var serverAddress = this.serverAddressProperties.getWebSocketServerAddress();
        assertTrue(StringUtils.isNotBlank(serverAddress));
        assertTrue(serverAddress.startsWith("ws://127.0.0.1:"));
    }

}
