package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User.
 * Предоставляет методы для выполнения операций с пользователями в базе данных,
 * включая стандартные CRUD-операции, а также специализированные запросы
 * для поиска, фильтрации и получения статистики по пользователям.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его имени пользователя (username).
     *
     * @param username имя пользователя для поиска
     * @return Optional, содержащий пользователя, если он существует,
     *         или пустой Optional, если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет существование пользователя с указанным именем пользователя.
     *
     * @param username имя пользователя для проверки
     * @return true, если пользователь с таким именем существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email email для проверки
     * @return true, если пользователь с таким email существует, иначе false
     */
    boolean existsByEmail(String email);

    /**
     * Находит всех пользователей с указанной ролью.
     *
     * @param role роль для фильтрации пользователей (STUDENT, TEACHER, ADMIN)
     * @return список пользователей с указанной ролью
     */
    List<User> findByRole(String role);

    /**
     * Подсчитывает количество пользователей с указанной ролью.
     *
     * @param role роль для подсчета
     * @return количество пользователей с указанной ролью
     */
    long countByRole(String role);

    /**
     * Подсчитывает количество пользователей, созданных после указанной даты.
     *
     * @param since дата, после которой подсчитываются пользователи
     * @return количество пользователей, созданных после указанной даты
     */
    long countByCreatedAtAfter(LocalDateTime since);

    /**
     * Находит топ преподавателей по количеству созданных курсов и количеству записей на эти курсы.
     * Результат упорядочен по убыванию количества курсов, а затем по убыванию количества записей.
     *
     * @param limit максимальное количество возвращаемых записей
     * @return список массивов объектов, где каждый массив содержит:
     *         - имя пользователя преподавателя (String)
     *         - количество созданных курсов (Long)
     *         - общее количество записей на все курсы преподавателя (Long)
     */
    @Query("SELECT u.username, COUNT(c), COALESCE(SUM(SIZE(c.enrollments)), 0) " +
            "FROM User u " +
            "LEFT JOIN u.taughtCourses c " +
            "WHERE u.role = 'TEACHER' " +
            "GROUP BY u.id, u.username " +
            "ORDER BY COUNT(c) DESC, COALESCE(SUM(SIZE(c.enrollments)), 0) DESC")
    List<Object[]> findTopTeachersWithStats(int limit);
}