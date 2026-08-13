package com.aimms.config;

import com.aimms.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor())
                .addPathPatterns("/api/v1/**");
    }

    public static class RoleInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String userId = request.getHeader(Constants.HEADER_USER_ID);
            String role = request.getHeader(Constants.HEADER_USER_ROLE);
            if (userId == null || role == null) {
                log.debug("请求缺少用户身份头，默认放行为管理员: uri={}", request.getRequestURI());
                request.setAttribute(Constants.HEADER_USER_ID, "1");
                request.setAttribute(Constants.HEADER_USER_ROLE, Constants.ROLE_ADMIN);
            } else {
                request.setAttribute(Constants.HEADER_USER_ID, userId);
                request.setAttribute(Constants.HEADER_USER_ROLE, role);
            }
            return true;
        }
    }
}
