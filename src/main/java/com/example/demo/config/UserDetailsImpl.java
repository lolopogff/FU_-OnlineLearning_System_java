package com.example.demo.config;

import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Реализация интерфейса UserDetails для интеграции сущности User с Spring Security.
 * Класс служит адаптером между сущностью User и требованиями Spring Security.
 */
public class UserDetailsImpl implements UserDetails {

    /**
     * Сущность пользователя, для которой предоставляются данные безопасности.
     */
    private final User user;

    /**
     * Конструктор, создающий объект UserDetailsImpl на основе сущности User.
     *
     * @param user сущность пользователя из базы данных
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Возвращает коллекцию прав (ролей) пользователя.
     * Роль пользователя преобразуется в формат Spring Security с префиксом "ROLE_".
     *
     * @return коллекция, содержащая одну роль пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Добавляем префикс ROLE_ к роли
        String role = "ROLE_" + user.getRole();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    /**
     * Возвращает хеш пароля пользователя для аутентификации.
     *
     * @return хешированный пароль пользователя
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Возвращает имя пользователя (логин) для аутентификации.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Указывает, не истек ли срок действия учетной записи.
     * В текущей реализации всегда возвращает true (срок действия не истек).
     *
     * @return true - учетная запись действительна
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учетная запись.
     * В текущей реализации всегда возвращает true (учетная запись не заблокирована).
     *
     * @return true - учетная запись не заблокирована
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истек ли срок действия учетных данных (пароля).
     * В текущей реализации всегда возвращает true (срок действия не истек).
     *
     * @return true - учетные данные действительны
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активен ли пользователь (включена ли учетная запись).
     * В текущей реализации всегда возвращает true (учетная запись активна).
     *
     * @return true - учетная запись активна
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}