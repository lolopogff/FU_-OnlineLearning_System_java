package com.example.demo.config;

import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурационный класс безопасности Spring Security.
 * Настраивает аутентификацию, авторизацию и защиту HTTP-запросов.
 *
 * <p>Аннотации класса:
 * <ul>
 *   <li>{@link Configuration} - определяет класс как конфигурационный Spring</li>
 *   <li>{@link EnableWebSecurity} - включает поддержку безопасности Spring Security</li>
 *   <li>{@link EnableMethodSecurity} - включает аннотационную безопасность методов
 *       с поддержкой pre/post атрибутов</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /** Сервис для загрузки данных пользователя в Spring Security */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Конструктор с внедрением зависимости сервиса пользователей.
     *
     * @param userDetailsService сервис для работы с данными пользователей
     */
    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Создает и настраивает цепочку фильтров безопасности.
     *
     * <p>Конфигурация включает:
     * <ul>
     *   <li>Отключение CSRF защиты (для упрощения, может быть включена для production)</li>
     *   <li>Настройку авторизации HTTP-запросов с разными правилами доступа</li>
     *   <li>Настройку формы логина с кастомной страницей входа</li>
     *   <li>Настройку выхода из системы (logout)</li>
     *   <li>Подключение провайдера аутентификации</li>
     * </ul>
     *
     * @param http объект для настройки веб-безопасности
     * @return сконфигурированная цепочка фильтров безопасности
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()
                        // Защищенные эндпоинты (требуют аутентификации)
                        .requestMatchers("/courses/**").authenticated()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Кастомная страница логина
                        .loginPage("/auth/login")
                        // URL для обработки формы логина
                        .loginProcessingUrl("/auth/login")
                        // Перенаправление после успешного входа
                        .defaultSuccessUrl("/courses/", true)
                        // Страница при ошибке входа
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        // URL для выхода
                        .logoutUrl("/logout")
                        // Перенаправление после успешного выхода
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                )
                // Подключение провайдера аутентификации
                .authenticationProvider(authenticationProvider())
                .build();
    }

    /**
     * Создает и настраивает провайдер аутентификации.
     *
     * <p>Использует {@link DaoAuthenticationProvider} для аутентификации
     * на основе данных из базы через {@link UserDetailsServiceImpl}.</p>
     *
     * @return настроенный провайдер аутентификации
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Установка сервиса для загрузки пользователей
        provider.setUserDetailsService(userDetailsService);
        // Установка кодировщика паролей
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    /**
     * Создает и возвращает кодировщик паролей.
     *
     * <p>Используется алгоритм хеширования BCrypt для безопасного хранения паролей.</p>
     *
     * @return кодировщик паролей на основе BCrypt
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}