package com.jewelry.pims;

import com.jewelry.pims.security.PasswordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void shouldMatchEncodedPassword() {
        String encoded = PasswordUtil.encode("admin123");
        Assertions.assertTrue(PasswordUtil.matches("admin123", encoded));
        Assertions.assertFalse(PasswordUtil.matches("wrong", encoded));
    }
}
