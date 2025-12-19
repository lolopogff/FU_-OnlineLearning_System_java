package com.example.demo.dto;

// TeacherStats.java

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherStats {
    private String username;
    private Long courseCount;
    private Long enrollmentCount;

    // Конструкторы, геттеры и сеттеры
    public TeacherStats() {}

}