package cn.sx.sxupr.module.iot.core.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public final class IotDeviceCredentialUtils {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private IotDeviceCredentialUtils() {
    }

    public static String generateDeviceKey() {
        return "dev_"
                + UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

    public static String generateDeviceSecret() {

        byte[] bytes = new byte[32];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}