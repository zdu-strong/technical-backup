package com.john.project.constant;

import java.time.Duration;

public class EncryptDecryptConstant {

    // Key rotation time
    public final static Duration SECRET_KEY_ROTATION_DURATION = Duration.ofDays(30);

    // Expiration time
    public final static Duration SECRET_KEY_EXPIRED_DURATION = Duration.ofDays(90);

    // Wait for new key activation delay
    public final static Duration SECRET_KEY_WAIT_ACTIVATION_DURATION = Duration.ofDays(3);

    public final static String ROUTINE_ROTATION = "ROUTINE_ROTATION_{}";

    public final static String EMERGENCY_ROTATION = "EMERGENCY_ROTATION_{}";

}
