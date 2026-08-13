package com.aimms.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RTest {

    @Test
    void okWithoutDataUsesSuccessDefaults() {
        R<Object> response = R.ok();

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        assertThat(response.getData()).isNull();
    }

    @Test
    void okWithDataStoresPayload() {
        R<String> response = R.ok("payload");

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("success");
        assertThat(response.getData()).isEqualTo("payload");
    }

    @Test
    void errorWithMessageUsesDefaultErrorCode() {
        R<Void> response = R.error("failed");

        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMessage()).isEqualTo("failed");
        assertThat(response.getData()).isNull();
    }

    @Test
    void errorWithCodeAndMessageUsesProvidedValues() {
        R<Void> response = R.error(403, "forbidden");

        assertThat(response.getCode()).isEqualTo(403);
        assertThat(response.getMessage()).isEqualTo("forbidden");
        assertThat(response.getData()).isNull();
    }
}
