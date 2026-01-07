package com.msa.auth.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    private String username;
    private String name;
    private String password;
    private Set<String> role;
}
