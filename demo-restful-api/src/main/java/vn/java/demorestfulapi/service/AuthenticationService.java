package vn.java.demorestfulapi.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import vn.java.demorestfulapi.dto.request.SignInRequest;
import vn.java.demorestfulapi.dto.response.SignInResponse;
import vn.java.demorestfulapi.dto.response.TokenResponse;
import vn.java.demorestfulapi.exception.InvalidDataException;
import vn.java.demorestfulapi.model.Token;
import vn.java.demorestfulapi.repository.UserRepository;

import java.util.List;

import static org.springframework.http.HttpHeaders.REFERER;
import static vn.java.demorestfulapi.util.TokenType.ACCESS_TOKEN;
import static vn.java.demorestfulapi.util.TokenType.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;


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

    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.getByUsername(signInRequest.getUsername());

//        List<String> roles = userService.findAllRolesByUserId(user.getId());
//        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // save token to db
        tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    /**
     * Refresh token
     *
     * @param request
     * @return
     */
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String refreshToken = request.getHeader(REFERER);
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
        var user = userService.getByUsername(userName);
        if (!jwtService.isValid(refreshToken, REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // save token to db
        tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    /**
     * Logout
     *
     * @param request
     * @return
     */
    public String logout(HttpServletRequest request) {
        log.info("---------- logout ----------");

        final String token = request.getHeader(REFERER);
        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);
        tokenService.delete(userName);

        return "Deleted!";
    }
}