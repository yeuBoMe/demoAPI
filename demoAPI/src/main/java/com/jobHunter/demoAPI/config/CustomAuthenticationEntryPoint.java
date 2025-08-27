package com.jobHunter.demoAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobHunter.demoAPI.domain.response.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Xử lý client gửi sai định dạng token
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        String errorMessage = Optional.ofNullable(authException.getCause())
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        restResponse.setError(errorMessage);
        restResponse.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền vào JWT ở header)...");

        objectMapper.writeValue(response.getWriter(), restResponse);
    }
}
