package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Основной контроллер для обработки корневого URL приложения.
 * Выполняет перенаправление на страницу аутентификации.
 */
@Controller
public class HomeController {

    /**
     * Обрабатывает запрос к корневому URL приложения.
     * Перенаправляет пользователя на страницу входа в систему.
     *
     * @return строка с командой перенаправления на страницу аутентификации
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}