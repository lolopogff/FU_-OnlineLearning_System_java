package com.example.demo.service;

import com.example.demo.dto.TeacherStats;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Обеспечивает бизнес-логику для операций с пользователями,
 * включая регистрацию, аутентификацию, получение статистики и управление ролями.
 */
@Service
@AllArgsConstructor
public class UserService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private UserRepository userRepository;

    /**
     * Кодировщик паролей для безопасного хранения паролей.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Получает текущего аутентифицированного пользователя.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return объект пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getCurrentUser(Authentication authentication) {
        return findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Сохраняет нового пользователя в системе.
     * Выполняет проверку уникальности имени пользователя и email.
     * Хеширует пароль перед сохранением.
     *
     * @param user объект пользователя для сохранения
     * @throws RuntimeException если имя пользователя или email уже существуют
     */
    public void save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        userRepository.save(user);
    }

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional, содержащий пользователя, если он существует,
     *         или пустой Optional, если пользователь не найден
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Возвращает всех пользователей с ролью TEACHER.
     *
     * @return список преподавателей
     */
    public List<User> getAllTeachers() {
        return userRepository.findByRole("TEACHER");
    }

    /**
     * Проверяет, имеет ли текущий аутентифицированный пользователь указанную роль.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @param role роль для проверки (например, "STUDENT", "TEACHER", "ADMIN")
     * @return true, если пользователь имеет указанную роль, иначе false
     */
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Возвращает общее количество пользователей в системе.
     *
     * @return количество пользователей
     */
    public Long getTotalUsersCount(){
        return userRepository.count();
    }

    /**
     * Возвращает количество пользователей с указанной ролью.
     *
     * @param role роль для фильтрации (например, "STUDENT", "TEACHER", "ADMIN")
     * @return количество пользователей с указанной ролью
     */
    public long getUsersCountByRole(String role) {
        return userRepository.countByRole(role);
    }

    /**
     * Возвращает количество новых пользователей, зарегистрированных за последние указанное количество дней.
     *
     * @param days количество дней для анализа
     * @return количество новых пользователей за указанный период
     */
    public long getNewUsersCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.countByCreatedAtAfter(since);
    }

    /**
     * Возвращает список топ преподавателей с ограничением по количеству.
     * Преподаватели сортируются по количеству созданных курсов и количеству записей на эти курсы.
     *
     * @param limit максимальное количество возвращаемых преподавателей
     * @return список объектов TeacherStats с данными о преподавателях
     */
    public List<TeacherStats> getTopTeachers(int limit) {
        List<Object[]> results = userRepository.findTopTeachersWithStats(limit);
        return results.stream().map(result -> {
            TeacherStats stats = new TeacherStats();
            stats.setUsername((String) result[0]);
            stats.setCourseCount((Long) result[1]);
            stats.setEnrollmentCount((Long) result[2]);
            return stats;
        }).collect(Collectors.toList());
    }
}