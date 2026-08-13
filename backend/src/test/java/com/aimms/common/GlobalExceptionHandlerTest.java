package com.aimms.common;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessExceptionReturnsBusinessCodeAndMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/budgets");
        BusinessException exception = new BusinessException(422, "quota exceeded");

        R<Void> response = handler.handleBusinessException(exception, request);

        assertThat(response.getCode()).isEqualTo(422);
        assertThat(response.getMessage()).isEqualTo("quota exceeded");
        assertThat(response.getData()).isNull();
    }

    @Test
    void handleValidationExceptionReturnsBadRequestResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/users");

        R<Void> response = handler.handleValidationException(new IllegalArgumentException("invalid"), request);

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("请求参数不合法");
        assertThat(response.getData()).isNull();
    }

    @Test
    void handleExceptionReturnsGenericServerErrorResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/dashboard");

        R<Void> response = handler.handleException(new RuntimeException("boom"), request);

        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMessage()).isEqualTo("系统繁忙，请稍后重试");
        assertThat(response.getData()).isNull();
    }
}
