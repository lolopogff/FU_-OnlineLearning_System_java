package com.example.demo.dto;

import com.example.demo.entity.Course;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) для передачи данных курса между слоями приложения.
 * Содержит основные поля курса для отображения и обмена данными.
 * Использует аннотации Lombok для автоматической генерации геттеров и сеттеров.
 */
@Getter
@Setter
public class CourseDTO {
    /**
     * Уникальный идентификатор курса.
     */
    private Long id;

    /**
     * Название курса.
     */
    private String title;

    /**
     * Подробное описание курса.
     */
    private String description;

    /**
     * Имя пользователя преподавателя, создавшего курс.
     */
    private String teacherUsername;

    /**
     * Стоимость курса. Может быть null для бесплатных курсов.
     */
    private BigDecimal price;

    /**
     * Категория или тематика курса.
     */
    private String category;

    /**
     * Дата и время создания курса.
     */
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления курса.
     */
    private LocalDateTime updatedAt;

    /**
     * Конструктор, создающий DTO на основе сущности Course.
     * Копирует значения полей из сущности в DTO для безопасного использования в представлениях.
     *
     * @param course сущность курса, на основе которой создается DTO
     */
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