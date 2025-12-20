package com.example.demo.repository;

import com.example.demo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью UserProfile.
 * Предоставляет методы для выполнения операций с профилями пользователей в базе данных,
 * включая стандартные CRUD-операции и специализированные запросы.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Находит профиль пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя, чей профиль необходимо найти
     * @return Optional, содержащий профиль пользователя, если он существует,
     *         или пустой Optional, если профиль не найден
     */
    Optional<UserProfile> findByUserId(Long userId);
}