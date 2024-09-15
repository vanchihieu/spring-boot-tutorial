package com.devteria.identityservice.configuration;

import com.devteria.identityservice.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {"/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // Đoạn mã này đăng ký JwtAuthenticationEntryPoint với Spring Security. JwtAuthenticationEntryPoint sẽ được sử dụng khi người dùng gửi một yêu cầu mà không có JWT hợp lệ hoặc không cung cấp JWT. (lỗi 401)
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    /**
     * @return Đoạn mã này định nghĩa một JwtAuthenticationConverter với chức năng chuyển đổi JWT thành một đối tượng Authentication. Nó đặc biệt chú trọng vào việc xử lý quyền hạn (authorities) từ JWT, và tự động thêm tiền tố "ROLE_" vào mỗi quyền hạn để tuân theo yêu cầu của Spring Security về định danh vai trò người dùng.
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() { // Đây là một phương thức trả về đối tượng JwtAuthenticationConverter. Đối tượng này được sử dụng để chuyển đổi một JWT thành một đối tượng Authentication, trong đó chứa các thông tin liên quan đến người dùng, như danh tính (identity) và quyền hạn (authorities).
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); // Tạo một đối tượng JwtGrantedAuthoritiesConverter. Đây là đối tượng được sử dụng để chuyển đổi các claims trong JWT thành các quyền hạn (authorities) mà Spring Security có thể hiểu được.
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter(); //  Tạo một đối tượng JwtAuthenticationConverter. Đây là đối tượng chính chịu trách nhiệm chuyển đổi một JWT thành một đối tượng Authentication có thể sử dụng trong Spring Security.
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter); // Thiết lập JwtGrantedAuthoritiesConverter vừa tạo để JwtAuthenticationConverter sử dụng khi chuyển đổi các quyền hạn từ JWT.

        return jwtAuthenticationConverter;
    }

//    @Bean
//    JwtDecoder jwtDecoder() {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}