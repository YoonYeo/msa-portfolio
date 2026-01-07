package com.msa.auth.controller;

import com.msa.auth.controller.dto.JwtResponse;
import com.msa.auth.controller.dto.LoginRequest;
import com.msa.auth.controller.dto.LoginResponse;
import com.msa.auth.controller.dto.SignupRequest;
import com.msa.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        LoginResponse loginResponse = authService.login(loginRequest, ipAddress, userAgent);

        Cookie refreshTokenCookie = createRefreshTokenCookie(loginResponse.getRefreshToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new JwtResponse(loginResponse.getAccessToken(), loginResponse.getName()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        JwtResponse jwtResponse = authService.refresh(refreshToken);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                        HttpServletResponse response) {
        authService.logout(refreshToken);

        Cookie cookie = createRefreshTokenCookie(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 로컬 개발시 false, 프로덕션에서는 true
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        return cookie;
    }
}
