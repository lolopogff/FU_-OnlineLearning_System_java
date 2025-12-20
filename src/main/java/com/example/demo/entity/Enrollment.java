package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись студента на курс.
 * Обеспечивает связь между студентом и курсом, фиксируя время записи.
 * Гарантирует уникальность комбинации студент-курс через ограничение уникальности в базе данных.
 */
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "course_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    /**
     * Уникальный идентификатор записи на курс.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Студент, записавшийся на курс.
     * Связь многие-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /**
     * Курс, на который записан студент.
     * Связь многие-к-одному с сущностью Course.
     * Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * Дата и время записи студента на курс.
     * Заполняется автоматически при создании записи.
     */
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    /**
     * Метод жизненного цикла, вызываемый перед сохранением новой сущности.
     * Устанавливает текущее время в поле enrolledAt.
     */
    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}