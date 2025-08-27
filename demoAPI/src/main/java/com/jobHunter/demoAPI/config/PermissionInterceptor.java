package com.jobHunter.demoAPI.config;

import com.jobHunter.demoAPI.domain.entity.Permission;
import com.jobHunter.demoAPI.domain.entity.Role;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.exception.custom.PermissionException;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.NoSuchElementException;

// nhiệm vụ chặn request trước khi tới controller để kiểm tra quyền (permission) của user
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public PermissionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    /* Hàm này sẽ chặn request trc khi xử lý
     * Dùng trc khi request chạy vào controller
     * */
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // BEST_MATCHING_PATTERN_ATTRIBUTE: hằng số Spring MVC dùng để lưu pattern mapping của request
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() ->
                new NoSuchElementException("User not found!")
        );

        User userGetByEmail = this.userService.getUserByEmail(email);

        if (userGetByEmail != null) {
            Role roleOfUser = userGetByEmail.getRole();
            if (roleOfUser != null) {
                List<Permission> permissions = roleOfUser.getPermissions();
                boolean isAllow = permissions.stream()
                        .anyMatch(item -> item.getApiPath().equals(path)
                                && item.getMethod().equals(httpMethod)
                        );

                if (!isAllow) {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }
            } else {
                throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
            }
        }

        return true;
    }
}