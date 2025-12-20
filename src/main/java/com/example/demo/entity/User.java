package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая пользователя системы.
 * Содержит основную информацию о пользователе, его роль в системе,
 * а также связи с созданными курсами и записями на курсы.
 * Автоматически управляет датой создания учетной записи.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя пользователя для входа в систему.
     * Максимальная длина 50 символов.
     */
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Уникальный email пользователя.
     * Используется для идентификации и восстановления доступа.
     */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * Хешированный пароль пользователя.
     * Хранится в защищенном виде в базе данных.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Имя пользователя.
     * Обязательное поле, максимальная длина 100 символов.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Фамилия пользователя.
     * Обязательное поле, максимальная длина 100 символов.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Роль пользователя в системе.
     * Возможные значения: STUDENT, TEACHER, ADMIN.
     * Значение по умолчанию - STUDENT.
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role = "STUDENT"; // STUDENT, TEACHER, ADMIN

    /**
     * Дата и время создания учетной записи пользователя.
     * Заполняется автоматически перед сохранением новой записи.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Список курсов, созданных пользователем как преподавателем.
     * Связь один-ко-многим с сущностью Course.
     * Каскадные операции применяются ко всем связанным курсам.
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> taughtCourses = new ArrayList<>();

    /**
     * Список записей пользователя на курсы как студента.
     * Связь один-ко-многим с сущностью Enrollment.
     * Каскадные операции применяются ко всем связанным записям.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Метод жизненного цикла, вызываемый перед сохранением новой сущности.
     * Устанавливает текущее время в поле createdAt.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Профиль пользователя с дополнительной информацией.
     * Связь один-к-одному с сущностью UserProfile.
     * Управляется каскадными операциями и загружается лениво.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    /**
     * Вспомогательный метод для получения профиля пользователя.
     * Если профиль не существует, создает новый экземпляр и связывает его с пользователем.
     *
     * @return объект UserProfile, связанный с пользователем
     */
    public UserProfile getProfile() {
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(this);
        }
        return profile;
    }

    /**
     * Устанавливает имя пользователя с удалением лишних пробелов.
     *
     * @param firstName имя пользователя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    /**
     * Устанавливает фамилию пользователя с удалением лишних пробелов.
     *
     * @param lastName фамилия пользователя
     */
    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }
}