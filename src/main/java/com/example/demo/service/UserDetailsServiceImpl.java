package com.example.demo.service;

import com.example.demo.config.UserDetailsImpl;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для загрузки данных пользователя в Spring Security.
 * Реализует интерфейс {@link UserDetailsService} для интеграции с системой аутентификации.
 * Использует репозиторий пользователей для поиска пользователя по имени пользователя (username).
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private UserRepository userRepository;

    /**
     * Загружает данные пользователя по имени пользователя.
     * Преобразует сущность {@link User} в объект {@link UserDetailsImpl},
     * который используется Spring Security для аутентификации и авторизации.
     *
     * @param username имя пользователя для поиска
     * @return объект UserDetailsImpl с данными пользователя и его правами
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}