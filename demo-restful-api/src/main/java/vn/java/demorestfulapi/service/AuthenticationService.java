package vn.java.demorestfulapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vn.java.demorestfulapi.dto.request.SignInRequest;
import vn.java.demorestfulapi.dto.response.SignInResponse;
import vn.java.demorestfulapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Phương thức signIn xử lý quy trình đăng nhập của người dùng như sau:
     * - Xác thực thông tin đăng nhập bằng authenticationManager.
     * - Tìm kiếm người dùng trong cơ sở dữ liệu sau khi xác thực thành công.
     * - Nếu người dùng tồn tại, tạo một JWT sử dụng jwtService.
     * - Trả về SignInResponse chứa JWT (accessToken), cùng với các thông tin khác như refreshToken, userId, phoneNumber, và role.
     * @param request
     * @return
     */
    public SignInResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var accessToken = jwtService.generateToken(user);
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken("refresh_token")
                .userId(1L)
                .phoneNumber("phoneNumber")
                .role("ROLE_USER")
                .build();
    }
}