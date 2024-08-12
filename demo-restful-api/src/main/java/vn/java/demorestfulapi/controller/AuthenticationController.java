package vn.java.demorestfulapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.java.demorestfulapi.dto.request.SignInRequest;
import vn.java.demorestfulapi.dto.response.SignInResponse;
import vn.java.demorestfulapi.dto.response.TokenResponse;
import vn.java.demorestfulapi.service.AuthenticationService;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/access")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest request) {
        return new ResponseEntity<>(authenticationService.authenticate(request), OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refreshToken(request), OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.logout(request), OK);
    }
}