package com.school.management.controller;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // Accepts "ADMIN", "TEACHER", or "STUDENT"
}