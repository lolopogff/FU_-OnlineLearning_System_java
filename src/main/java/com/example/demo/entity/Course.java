package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая курс в системе.
 * Содержит информацию о курсе, включая название, описание, преподавателя, цену и категорию.
 * Автоматически управляет датами создания и обновления через методы жизненного цикла.
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    /**
     * Уникальный идентификатор курса.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название курса. Обязательное поле.
     * Должно содержать от 3 до 255 символов.
     */
    @NotNull(message = "Название курса не может быть пустым")
    @Size(min = 3, max = 255, message = "Название курса должно содержать от 3 до 255 символов")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Подробное описание курса.
     * Хранится как TEXT в базе данных для поддержки длинного содержимого.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Преподаватель, который создал и ведет курс.
     * Связь многие-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    /**
     * Цена курса. Может быть нулевой для бесплатных курсов.
     * Значение по умолчанию - 0.00.
     * Имеет ограничения: не может быть отрицательной, формат - до 10 цифр до запятой и 2 после.
     */
    @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Цена должна быть в формате: до 10 цифр до запятой и 2 после")
    @Column(name = "price", precision = 10000000, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Категория курса (например, "Программирование", "Дизайн", "Маркетинг").
     * Максимальная длина - 100 символов.
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * Дата и время создания записи о курсе.
     * Заполняется автоматически при создании.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи о курсе.
     * Заполняется автоматически при обновлении.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Список записей на этот курс (связь с сущностью Enrollment).
     * Каскадные операции применяются ко всем связанным записям.
     * Загружается лениво для оптимизации производительности.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Метод жизненного цикла, вызываемый перед сохранением новой сущности.
     * Устанавливает текущее время в поля createdAt и updatedAt.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Метод жизненного цикла, вызываемый перед обновлением сущности.
     * Обновляет поле updatedAt текущим временем.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}