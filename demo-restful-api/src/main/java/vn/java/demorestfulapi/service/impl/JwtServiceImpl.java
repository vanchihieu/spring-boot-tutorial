package vn.java.demorestfulapi.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.java.demorestfulapi.service.JwtService;
import vn.java.demorestfulapi.util.TokenType;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static vn.java.demorestfulapi.util.TokenType.ACCESS_TOKEN;
import static vn.java.demorestfulapi.util.TokenType.REFRESH_TOKEN;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }



    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(new HashMap<>(), user);
    }

    @Override
    public boolean isValid(String token, TokenType type, UserDetails userDetails) {
        final String username = extractUsername(token, type);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, type));
    }

    /**
     * Phương thức này được sử dụng để trích xuất một giá trị cụ thể từ các claims trong JWT.
     * Giá trị này được xác định bằng một Function (claimsResolver), một hàm nhận vào Claims và trả về một giá trị kiểu T.
     *
     * @param token:           Chuỗi JWT từ đó cần trích xuất thông tin.
     * @param claimsResolvers: Một hàm nhận Claims và trả về giá trị mong muốn từ các claims.
     * @param <T>
     * @return
     */
    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        final Claims claims = extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Thêm các thông tin khác vào JWT payload.(như userId, phoneNumber, role, ...)
                .setSubject(userDetails.getUsername()) // Đặt thông tin người dùng vào JWT payload.
                .setIssuedAt(new Date(System.currentTimeMillis())) // Đặt thời gian phát hành cho JWT.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Đặt thời gian hết hạn cho JWT.
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256).compact(); // Ký JWT bằng thuật toán HS256 và secretKey. Cuối cùng, chuyển đổi JWT thành chuỗi.
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        if (ACCESS_TOKEN.equals(type))
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        else
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
    }

    private boolean isTokenExpired(String token, TokenType type) {
        return extractExpiration(token, type).before(new Date());
    }

    private Date extractExpiration(String token, TokenType type) {
        return extractClaim(token, type, Claims::getExpiration);
    }


    /**
     * Phương thức này được sử dụng để trích xuất toàn bộ claims từ JWT.
     * <p>
     * Hoạt động:
     * - Phương thức sử dụng Jwts.parserBuilder() để tạo ra một JWT parser.
     * - Sau đó, nó thiết lập SigningKey bằng cách gọi getSigningKey() (đây là key được dùng để ký JWT khi tạo nó).
     * - Cuối cùng, nó gọi parseClaimsJws(token) để phân tích JWT và trả về phần claims của JWT bằng phương thức getBody().
     * <p>
     * Kết quả: Claims là một đối tượng chứa tất cả các thông tin được mã hóa trong JWT (như subject, issuedAt, expiration, và các thông tin tùy chỉnh khác).
     *
     * @return
     */
    private Claims extraAllClaim(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }


}
