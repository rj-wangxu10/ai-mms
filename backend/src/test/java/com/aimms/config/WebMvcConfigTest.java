package com.aimms.config;

import com.aimms.common.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class WebMvcConfigTest {

    private final WebMvcConfig.RoleInterceptor interceptor = new WebMvcConfig.RoleInterceptor();

    @Test
    void roleInterceptorDefaultsMissingIdentityHeadersToAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/dashboard");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(request.getAttribute(Constants.HEADER_USER_ID)).isEqualTo("1");
        assertThat(request.getAttribute(Constants.HEADER_USER_ROLE)).isEqualTo(Constants.ROLE_ADMIN);
    }

    @Test
    void roleInterceptorCopiesProvidedIdentityHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/dashboard");
        request.addHeader(Constants.HEADER_USER_ID, "42");
        request.addHeader(Constants.HEADER_USER_ROLE, Constants.ROLE_MANAGER);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(request.getAttribute(Constants.HEADER_USER_ID)).isEqualTo("42");
        assertThat(request.getAttribute(Constants.HEADER_USER_ROLE)).isEqualTo(Constants.ROLE_MANAGER);
    }
}
