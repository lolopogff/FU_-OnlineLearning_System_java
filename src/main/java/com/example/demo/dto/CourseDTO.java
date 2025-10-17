package com.example.demo.dto;
import com.example.demo.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseDTO {
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

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTeacherUsername() { return teacherUsername; }
    public void setTeacherUsername(String teacherUsername) { this.teacherUsername = teacherUsername; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}