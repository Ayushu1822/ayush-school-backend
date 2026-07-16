package com.school.management.controller;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}