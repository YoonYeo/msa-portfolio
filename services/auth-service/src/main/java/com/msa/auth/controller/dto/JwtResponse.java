package com.msa.auth.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String name;

    public JwtResponse(String accessToken, String name) {
        this.accessToken = accessToken;
        this.name = name;
    }
}
