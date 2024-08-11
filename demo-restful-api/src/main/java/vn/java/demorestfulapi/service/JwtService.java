package vn.java.demorestfulapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.java.demorestfulapi.util.TokenType;

public interface JwtService {
    String extractUsername(String token, TokenType type);

    String generateRefreshToken(UserDetails user);

    String generateToken(UserDetails userDetails);

    boolean isValid(String token, TokenType type, UserDetails user);
}