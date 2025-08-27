


package com.jobHunter.demoAPI.config;

import com.jobHunter.demoAPI.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// cấu hình spring để đăng ký interceptor vào spring mvc (giống như add middleware)
@Configuration
public class PermissionInterceptorConfig implements WebMvcConfigurer {

    private final UserService userService;

    public PermissionInterceptorConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> whiteList = List.of(
                "/",
                "/api/v1/auth/**",
                "/upload/**",
                "/api/v1/companies/**",
                "/api/v1/jobs/**",
                "/api/v1/skills/**",
                "/api/v1/subscribers/**",
                "/api/v1/resumes/**",
                "/api/v1/files"
        );

        // all requests must go through the interceptor exclude whitelist
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
