package com.example.demo.dto;

// TeacherStats.java

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления статистики преподавателя.
 * Используется для передачи данных о количестве курсов и записей на курсы преподавателя.
 */
@Setter
@Getter
public class TeacherStats {
    /**
     * Имя пользователя преподавателя.
     */
    private String username;

    /**
     * Количество курсов, созданных преподавателем.
     */
    private Long courseCount;

    /**
     * Общее количество записей на курсы преподавателя.
     */
    private Long enrollmentCount;

    /**
     * Конструкторы, геттеры и сеттеры
     */
    public TeacherStats() {}
}