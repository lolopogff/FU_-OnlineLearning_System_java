package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String bio;
    private String phone;
    private String profilePicture;
    private String location;
    private LocalDate dateOfBirth;
    private String website;
    private Map<String, String> socialLinks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
