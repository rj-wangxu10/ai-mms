package com.aimms.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void messageOnlyConstructorUsesDefaultCode() {
        BusinessException exception = new BusinessException("bad request");

        assertThat(exception.getCode()).isEqualTo(500);
        assertThat(exception.getMessage()).isEqualTo("bad request");
    }

    @Test
    void codeAndMessageConstructorStoresProvidedValues() {
        BusinessException exception = new BusinessException(409, "conflict");

        assertThat(exception.getCode()).isEqualTo(409);
        assertThat(exception.getMessage()).isEqualTo("conflict");
    }
}
