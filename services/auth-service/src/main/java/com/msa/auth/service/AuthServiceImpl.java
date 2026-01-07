package com.msa.auth.service;

import com.msa.auth.controller.dto.JwtResponse;
import com.msa.auth.controller.dto.LoginRequest;
import com.msa.auth.controller.dto.LoginResponse;
import com.msa.auth.controller.dto.SignupRequest;
import com.msa.auth.exception.*;
import com.msa.auth.model.ERole;
import com.msa.auth.model.Role;
import com.msa.auth.model.User;
import com.msa.auth.provider.JwtProvider;
import com.msa.auth.repository.RoleRepository;
import com.msa.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisSessionService redisSessionService;

    @Override
    public void signup(SignupRequest signupRequest) {
        log.info("회원가입 요청 아이디: {}", signupRequest.getUsername());

        // 중복 사용자 체크
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new DuplicateUserException("이미 존재하는 사용자입니다: " + signupRequest.getUsername());
        }

        // 사용자 생성
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setName(signupRequest.getName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // 기본 권한 부여
        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("권한을 찾을 수 없습니다."));
        user.getRoles().add(role);

        userRepository.save(user);
        log.info("회원가입 성공: {}", signupRequest.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest, String ipAddress, String userAgent) {
        log.info("로그인 시도 아이디: {}", loginRequest.getUsername());

        // 사용자 조회
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginRequest.getUsername()));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        // 권한 목록 추출
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        // 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getUsername(), roles);
        String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

        // Redis에 로그인 세션 저장 (기존 세션 자동 삭제 - 중복 로그인 방지)
        redisSessionService.saveLoginSession(
                user.getUsername(),
                user.getId(),
                user.getName(),
                roles,
                refreshToken,
                ipAddress,
                userAgent
        );

        log.info("로그인 성공: {}", loginRequest.getUsername());
        return new LoginResponse(accessToken, refreshToken, user.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponse refresh(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtProvider.getUsernameFromRefreshToken(refreshToken);

        // Redis에 저장된 리프레시 토큰과 비교 (중복 로그인 체크)
        if (!redisSessionService.validateRefreshToken(username, refreshToken)) {
            throw new InvalidTokenException("다른 곳에서 로그인되었습니다.");
        }

        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 권한 목록 추출
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(user.getUsername(), roles);

        log.info("리프레시 토큰 성공: {}", username);
        return new JwtResponse(newAccessToken, user.getName());
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
            String username = jwtProvider.getUsernameFromRefreshToken(refreshToken);
            redisSessionService.deleteSession(username);
            log.info("로그아웃 성공: {}", username);
        }
    }
}
