package com.msa.auth.service;

import com.msa.auth.controller.dto.JwtResponse;
import com.msa.auth.controller.dto.LoginRequest;
import com.msa.auth.controller.dto.LoginResponse;
import com.msa.auth.controller.dto.SignupRequest;

public interface AuthService {
    void signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest, String ipAddress, String userAgent);

    JwtResponse refresh(String refreshToken);

    void logout(String refreshToken);
}
