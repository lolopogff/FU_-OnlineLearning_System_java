package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class UpdateProfileRequest {
    private String bio;
    private String phone;
    private String location;
    private LocalDate dateOfBirth;
    private String website;
    private Map<String, String> socialLinks;

    // Геттеры и сеттеры
}
