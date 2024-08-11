package vn.java.demorestfulapi.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.java.demorestfulapi.service.JwtService;
import vn.java.demorestfulapi.service.UserService;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static vn.java.demorestfulapi.util.TokenType.ACCESS_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Đoạn mã này là một bộ lọc bảo mật Spring Security sử dụng JWT để xác thực người dùng.
     * Nó trích xuất JWT từ header Authorization, kiểm tra tính hợp lệ của token, và nếu hợp lệ, nó thiết lập thông tin xác thực cho người dùng trong SecurityContextHolder, cho phép họ truy cập các tài nguyên được bảo vệ bởi Spring Security trong ứng dụng.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("------------- PreFilter -------------");

        final String authorization = request.getHeader(AUTHORIZATION); // Lấy giá trị của header Authorization từ yêu cầu HTTP. Đây thường là nơi chứa JWT.
        /*
            * Kiểm tra xem header Authorization có giá trị không hoặc không bắt đầu bằng "Bearer " không.
            * Nếu không thì bỏ qua và chuyển tiếp yêu cầu đến Filter tiếp theo.
         */
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring("Bearer ".length()); // Cắt bỏ phần tiền tố "Bearer " để lấy JWT thực tế từ header Authorization.
        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);


        /**
         * Kiểm tra xem userName có giá trị không và người dùng chưa được xác thực không.
         * Nếu có thì tạo ra một UserDetails từ userName và kiểm tra xem JWT có hợp lệ không.
         * Nếu hợp lệ thì tạo ra một Authentication từ UserDetails và lưu vào SecurityContext.
         * Cuối cùng chuyển tiếp yêu cầu đến Filter tiếp theo.
         * Nếu không thì bỏ qua và chuyển tiếp yêu cầu đến Filter tiếp theo.
         */
        if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);
            if (jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext(); // Tạo một SecurityContext trống để lưu trữ thông tin xác thực mới.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Tạo một đối tượng UsernamePasswordAuthenticationToken để chứa thông tin xác thực của người dùng. Thông tin này bao gồm thông tin người dùng (userDetails), không có mật khẩu (null), và các quyền hạn (authorities) của người dùng.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken); // Đặt authToken vào SecurityContext, nghĩa là người dùng hiện tại đã được xác thực và có thể thực hiện các hành động dựa trên quyền hạn của họ.
                SecurityContextHolder.setContext(context); // Lưu SecurityContext mới này vào SecurityContextHolder để nó có thể được sử dụng trong phần còn lại của ứng dụng trong suốt vòng đời của yêu cầu này.
            }
        }
        filterChain.doFilter(request, response); // Chuyển tiếp yêu cầu tới bộ lọc tiếp theo trong chuỗi. Đây là bước cuối cùng để tiếp tục xử lý yêu cầu sau khi đã thực hiện xác thực thành công.
    }
}