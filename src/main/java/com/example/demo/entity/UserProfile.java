package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая профиль пользователя с дополнительной информацией.
 * Содержит расширенные данные о пользователе, такие как биография, контакты,
 * фотография профиля и другая персональная информация.
 * Связана с сущностью User отношением один-к-одному.
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    /**
     * Уникальный идентификатор профиля пользователя.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, к которому относится этот профиль.
     * Связь один-к-одному с сущностью User.
     * Загружается лениво для оптимизации производительности.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Биография пользователя.
     * Может содержать развернутую информацию о пользователе, его опыте и интересах.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Номер телефона пользователя.
     * Максимальная длина 20 символов.
     */
    @Column(length = 20)
    private String phone;

    /**
     * Путь к файлу с фотографией профиля пользователя.
     * Может быть относительным путем в файловой системе или URL.
     */
    @Column(name = "profile_picture")
    private String profilePicture;

    /**
     * Местоположение пользователя.
     * Может содержать город, страну или другой географический идентификатор.
     * Максимальная длина 100 символов.
     */
    @Column(length = 100)
    private String location;

    /**
     * Дата рождения пользователя.
     * Используется для отображения возраста и персональной информации.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Веб-сайт пользователя.
     * Может содержать ссылку на личный сайт, блог или профиль в социальных сетях.
     */
    private String website;

    /**
     * Дата и время создания профиля пользователя.
     * Заполняется автоматически при создании.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления профиля пользователя.
     * Заполняется автоматически при обновлении.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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