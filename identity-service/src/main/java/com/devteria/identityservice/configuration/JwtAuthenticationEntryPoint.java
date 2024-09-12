package com.devteria.identityservice.configuration;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 *  Khi người dùng gửi một yêu cầu đến ứng dụng mà không có JWT hợp lệ hoặc không cung cấp JWT, Spring Security sẽ gọi phương thức commence.
 *  Lúc này, JwtAuthenticationEntryPoint sẽ tạo phản hồi JSON bao gồm mã lỗi và thông điệp lỗi (ví dụ: "Unauthorized" hoặc "Unauthenticated").
 *  Phản hồi này sau đó sẽ được gửi lại cho người dùng kèm mã trạng thái HTTP 401.
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}