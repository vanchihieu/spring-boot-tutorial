package vn.java.demorestfulapi.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.java.demorestfulapi.service.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@Profile("!prod")
@EnableWebSecurity
@RequiredArgsConstructor
public class AppConfig {
    private final PreFilter preFilter;
    private final UserService userService;

    /**
     * Cấu hình CORS (Cross-Origin Resource Sharing) cho ứng dụng Spring Boot. CORS là một cơ chế cho phép tài nguyên web từ một domain khác được yêu cầu từ domain hiện tại.
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("**")
                        .allowedOrigins("http://localhost:8500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                        .allowedHeaders("*") // Allowed request headers
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    /**
     * Cấu hình Spring Security để bỏ qua các tài nguyên không cần xác thực.
     * @return
     */
    @Bean
    public WebSecurityCustomizer ignoreResources() {
        return (webSecurity) -> webSecurity
                .ignoring()
                .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // dùng STATELESS để không lưu session trong server
                .authenticationProvider(authenticationProvider()).addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class); // thêm preFilter vào trước UsernamePasswordAuthenticationFilter để xử lý token trước khi xác thực thông tin đăng nhập
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // DaoAuthenticationProvider là một lớp cung cấp cơ chế xác thực dựa trên việc tải thông tin người dùng từ một UserDetailsService và so sánh mật khẩu được cung cấp với mật khẩu đã được mã hóa.
        /**
         * Thiết lập UserDetailsService cho DaoAuthenticationProvider.
         * UserDetailsService là một interface của Spring Security, được sử dụng để tải thông tin người dùng (thường là từ cơ sở dữ liệu) dựa trên tên người dùng.
         * Trong trường hợp này, bạn đang sử dụng userService.userDetailsService() để trả về một đối tượng UserDetailsService tùy chỉnh được cung cấp bởi UserService.
         */
        authProvider.setUserDetailsService(userService.userDetailsService());

        /**
         * Thiết lập một PasswordEncoder cho DaoAuthenticationProvider.
         * PasswordEncoder là một interface được sử dụng để mã hóa mật khẩu (ví dụ: bằng cách sử dụng BCrypt) khi lưu trữ và kiểm tra mật khẩu trong quá trình xác thực.
         * passwordEncoder() có thể là một phương thức khác trong cấu hình của bạn, tạo ra một đối tượng PasswordEncoder, chẳng hạn như BCryptPasswordEncoder.
         */
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}