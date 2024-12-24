package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.AuthenticationRequest;
import com.devteria.identityservice.dto.request.IntrospectRequest;
import com.devteria.identityservice.dto.request.LogoutRequest;
import com.devteria.identityservice.dto.request.RefreshRequest;
import com.devteria.identityservice.dto.response.AuthenticationResponse;
import com.devteria.identityservice.dto.response.IntrospectResponse;
import com.devteria.identityservice.entity.InvalidatedToken;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.repository.InvalidatedTokenRepository;
import com.devteria.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    // Kiểm tra token có hợp lệ hay không
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException { // kiểm tra token có hợp lệ không
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false); // Xác thực token mà không sử dụng khả năng refresh
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // Xác thực người dùng thông qua kiểm tra tên đăng nhập và mật khẩu
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra mật khẩu đã cung cấp có khớp với mật khẩu trong cơ sở dữ liệu không
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user); // Tạo JWT token cho người dùng đã xác thực thành công

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // Vô hiệu hóa token bằng cách thêm nó vào kho lưu trữ token bị hủy
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true); // Xác thực token với khả năng refresh token

            // Lấy JWT ID và thời gian hết hạn để vô hiệu hóa token
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            // Lưu thông tin token đã bị vô hiệu hóa
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception){
            log.info("Token already expired");
        }
    }

    // Tạo JWT token cho một người dùng cụ thể
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // thuật toán mã hóa HS512

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // Xây dựng nội dung JWT
                .subject(user.getUsername()) // chủ thể của token
                .issuer("chihieu.com") // người tạo ra token
                .issueTime(new Date()) // thời gian tạo ra token
                .expirationTime(new Date( // thời gian hết hạn token
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString()) // id của token
                .claim("scope", buildScope(user)) // thêm thông tin vào token
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());  // Chuyển đổi nội dung thành payload

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes())); // Ký token bằng khóa bí mật
            return jwsObject.serialize();  // Chuyển token thành chuỗi
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    // Làm mới token hiện tại và vô hiệu hóa token cũ
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // lấy thông tin từ token và đưa vào invalidated token
        var signedJWT = verifyToken(request.getToken(), true); // Xác thực token với khả năng refresh token

        // Lấy JWT ID và thời gian hết hạn để vô hiệu hóa
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken); // Lưu thông tin token đã bị vô hiệu hóa

        // Lấy thông tin người dùng và tạo token mới
        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    // Xác thực tính hợp lệ của token và kiểm tra thời gian hết hạn
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes()); // Tạo trình xác thực bằng khóa bí mật

        SignedJWT signedJWT = SignedJWT.parse(token); // Phân tích token được cung cấp

        // Xác định thời gian hết hạn dựa trên việc có phải yêu cầu refresh hay không
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        // Kiểm tra tính hợp lệ và thời gian hết hạn của token
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Kiểm tra xem token đã bị vô hiệu hóa chưa
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT; // Trả về token đã được xác thực
    }

    // Xây dựng chuỗi vai trò và quyền hạn của người dùng
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        // Thêm vai trò và quyền hạn vào chuỗi scope
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString(); // vd: ROLE_ADMIN READ WRITE ROLE_USER VIEW

    }
}
