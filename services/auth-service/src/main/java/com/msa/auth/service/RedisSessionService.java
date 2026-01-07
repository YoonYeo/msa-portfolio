package com.msa.auth.service;

import com.msa.auth.model.LoginSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "session:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long SESSION_TTL_SECONDS = 7 * 24 * 60 * 60; // 7일

    /**
     * 로그인 세션 저장 (기존 세션이 있으면 덮어쓰기 - 중복 로그인 방지)
     */
    public void saveLoginSession(String username, Long userId, String name, List<String> roles,
                                   String refreshToken, String ipAddress, String userAgent) {

        // 기존 세션 삭제 (중복 로그인 방지)
        deleteSession(username);

        LoginSession session = new LoginSession();
        session.setUserId(userId);
        session.setUsername(username);
        session.setName(name);
        session.setRoles(roles);
        session.setRefreshToken(refreshToken);
        session.setLoginTime(LocalDateTime.now());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);

        // 세션 정보 저장
        String sessionKey = SESSION_PREFIX + username;
        redisTemplate.opsForValue().set(sessionKey, session, SESSION_TTL_SECONDS, TimeUnit.SECONDS);

        // 리프레시 토큰 별도 저장 (빠른 검증용)
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, SESSION_TTL_SECONDS, TimeUnit.SECONDS);

        log.info("Login session saved for user: {} from IP: {}", username, ipAddress);
    }

    /**
     * 리프레시 토큰 검증
     */
    public boolean validateRefreshToken(String username, String refreshToken) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        Object storedToken = redisTemplate.opsForValue().get(refreshTokenKey);

        if (storedToken == null) {
            log.warn("No refresh token found for user: {}", username);
            return false;
        }

        boolean isValid = storedToken.equals(refreshToken);
        if (!isValid) {
            log.warn("Invalid refresh token for user: {}", username);
        }

        return isValid;
    }

    /**
     * 세션 조회
     */
    public LoginSession getSession(String username) {
        String sessionKey = SESSION_PREFIX + username;
        return (LoginSession) redisTemplate.opsForValue().get(sessionKey);
    }

    /**
     * 로그아웃 (세션 삭제)
     */
    public void deleteSession(String username) {
        String sessionKey = SESSION_PREFIX + username;
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;

        redisTemplate.delete(sessionKey);
        redisTemplate.delete(refreshTokenKey);

        log.info("Session deleted for user: {}", username);
    }

    /**
     * 세션 존재 확인
     */
    public boolean hasSession(String username) {
        String sessionKey = SESSION_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }
}
