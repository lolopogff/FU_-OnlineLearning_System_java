package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурационный класс для настройки MVC (Model-View-Controller) в приложении.
 * Реализует интерфейс {@link WebMvcConfigurer} для кастомизации конфигурации Spring MVC.
 *
 * <p>Класс помечен аннотацией {@link Configuration}, что указывает Spring на то,
 * что этот класс содержит определения бинов и конфигурационные настройки.</p>
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Регистрирует контроллеры представлений для обработки запросов.
     *
     * <p>Метод переопределяет {@link WebMvcConfigurer#addViewControllers(ViewControllerRegistry)}
     * и позволяет зарегистрировать простые автоматизированные контроллеры предварительно
     * сконфигурированных статусных кодов и/или представлений.</p>
     *
     * <p>В текущей реализации метод оставлен пустым, что означает отсутствие специальной
     * регистрации контроллеров представлений. Это может быть использовано для будущего
     * расширения функциональности.</p>
     *
     * @param registry реестр для регистрации контроллеров представлений
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Реализация может быть добавлена в будущем для регистрации контроллеров представлений
    }

}