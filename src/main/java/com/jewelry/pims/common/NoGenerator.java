package com.jewelry.pims.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class NoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private NoGenerator() {
    }

    public static String generate(String prefix) {
        return prefix + FORMATTER.format(LocalDateTime.now()) + ThreadLocalRandom.current().nextInt(100, 999);
    }
}
