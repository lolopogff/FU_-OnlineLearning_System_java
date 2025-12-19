package com.example.demo.dto;
import com.example.demo.entity.Course;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CourseDTO {
    // Геттеры и сеттеры
    private Long id;
    private String title;
    private String description;
    private String teacherUsername;
    private BigDecimal price;
    private String category;
    private String level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Конструктор для преобразования Course в CourseDTO
    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.teacherUsername = course.getTeacher() != null ? course.getTeacher().getUsername() : null;
        this.price = course.getPrice();
        this.category = course.getCategory();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
    }

}