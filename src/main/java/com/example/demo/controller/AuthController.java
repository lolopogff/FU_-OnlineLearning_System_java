package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 * Обеспечивает функционал входа в систему и регистрации новых пользователей.
 */
@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    /**
     * Сервис для работы с пользователями.
     */
    private UserService userService;

    /**
     * Отображает страницу входа в систему.
     *
     * @return имя шаблона страницы входа "user/login"
     */
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    /**
     * Отображает форму регистрации нового пользователя.
     * Добавляет в модель пустой объект User для заполнения в форме.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона формы регистрации "user/register"
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    /**
     * Обрабатывает отправку формы регистрации нового пользователя.
     * Сохраняет нового пользователя в системе и перенаправляет на страницу входа.
     * В случае ошибки возвращает на форму регистрации с сообщением об ошибке.
     *
     * @param user объект User с данными из формы регистрации
     * @param model объект Model для передачи данных в представление
     * @return перенаправление на страницу входа при успешной регистрации
     *         или возврат на форму регистрации с ошибкой
     */
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            // Теперь форма предоставляет только STUDENT или TEACHER,
            // поэтому дополнительная валидация не нужна
            userService.save(user);
            return "redirect:/auth/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating user: " + e.getMessage());
            model.addAttribute("user", user);
            return "user/register";
        }
    }
}