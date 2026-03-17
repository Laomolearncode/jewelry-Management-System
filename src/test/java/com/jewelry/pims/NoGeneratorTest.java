package com.jewelry.pims;

import com.jewelry.pims.common.NoGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NoGeneratorTest {

    @Test
    void shouldGeneratePrefixedNo() {
        String no = NoGenerator.generate("PO");
        Assertions.assertTrue(no.startsWith("PO"));
        Assertions.assertTrue(no.length() > 10);
    }
}
