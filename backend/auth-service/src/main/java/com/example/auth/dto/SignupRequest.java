package com.example.auth.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}
