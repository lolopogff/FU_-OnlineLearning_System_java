package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

/**
 * Контроллер для управления профилями пользователей.
 * Обрабатывает операции просмотра, редактирования профиля, загрузки изображений
 * и предоставления публичного доступа к профилям пользователей.
 * Контроллер имеет имя "customProfileController" для явного указания в Spring контексте.
 */
@Controller("customProfileController")
@RequestMapping("/user")
public class ProfileController {

    /**
     * Сервис для работы с профилями пользователей.
     */
    private final UserProfileService userProfileService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Конструктор с внедрением зависимостей сервисов.
     *
     * @param userProfileService сервис для работы с профилями пользователей
     * @param userService сервис для работы с пользователями
     */
    public ProfileController(UserProfileService userProfileService, UserService userService) {
        this.userProfileService = userProfileService;
        this.userService = userService;
    }

    /**
     * Отображает профиль текущего пользователя.
     * Включает информацию о пользователе, его профиле и ролях.
     *
     * @param principal объект Principal для получения имени текущего пользователя
     * @param authentication объект Authentication для проверки ролей пользователя
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона для отображения профиля "user/profile/view" или перенаправление на страницу входа
     */
    @GetMapping("/profile")
    public String viewProfile(Principal principal, Authentication authentication, Model model) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());


            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            // Убедитесь, что hasProfilePicture правильно вычисляется
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));

            return "user/profile/view";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    /**
     * Отображает форму для редактирования профиля пользователя.
     *
     * @param principal объект Principal для получения имени текущего пользователя
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона для редактирования профиля "user/profile/edit" или перенаправление на страницу входа
     */
    @GetMapping("/profile/edit")
    public String editProfileForm(Principal principal, Model model) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());

            return "user/profile/edit";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    /**
     * Обновляет профиль пользователя на основе данных из формы редактирования.
     * Обрабатывает обновление основной информации, данных профиля и загрузку изображения.
     *
     * @param principal объект Principal для получения имени текущего пользователя
     * @param bio новая биография пользователя
     * @param phone новый телефон пользователя
     * @param location новое местоположение пользователя
     * @param dateOfBirth новая дата рождения пользователя (строка в формате yyyy-MM-dd)
     * @param website новый веб-сайт пользователя
     * @param firstName новое имя пользователя
     * @param lastName новая фамилия пользователя
     * @param file файл с новым изображением профиля (опционально)
     * @param redirectAttributes атрибуты для передачи сообщений при перенаправлении
     * @return перенаправление на страницу профиля или форму редактирования в случае ошибки
     */
    @PostMapping("/profile/edit")
    public String updateProfile(Principal principal,
                                @RequestParam String bio,
                                @RequestParam String phone,
                                @RequestParam String location,
                                @RequestParam(required = false) String dateOfBirth,
                                @RequestParam String website,
                                @RequestParam String firstName,  // Добавьте это
                                @RequestParam String lastName,   // Добавьте это
                                @RequestParam(value = "profilePicture", required = false) MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Сначала обновляем базовую информацию (имя и фамилия)
            userProfileService.updateUserBasicInfo(user.getId(), firstName, lastName);

            // Затем обновляем профиль
            userProfileService.updateUserProfile(
                    user.getId(),
                    bio,
                    phone,
                    location,
                    dateOfBirth != null && !dateOfBirth.isEmpty() ?
                            java.time.LocalDate.parse(dateOfBirth) : null,
                    website
            );

            // Если есть файл, сохраняем его
            if (file != null && !file.isEmpty()) {
                userProfileService.saveProfilePicture(user.getId(), file);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/user/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }

    /**
     * Обрабатывает загрузку изображения профиля пользователя.
     *
     * @param principal объект Principal для получения имени текущего пользователя
     * @param file файл с изображением для загрузки
     * @param redirectAttributes атрибуты для передачи сообщений при перенаправлении
     * @return перенаправление на страницу профиля пользователя
     */
    @PostMapping("/profile/upload-picture")
    public String uploadProfilePicture(Principal principal,
                                       @RequestParam("profilePicture") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!file.isEmpty()) {
                userProfileService.saveProfilePicture(user.getId(), file);
                redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            }

            return "redirect:/user/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading picture: " + e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Отображает публичный профиль пользователя по имени пользователя.
     *
     * @param username имя пользователя, чей профиль нужно отобразить
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона публичного профиля "user/profile/public" или перенаправление на список курсов
     */
    @GetMapping("/profile/public/{username}")
    public String viewPublicProfile(@PathVariable String username, Model model) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());

            return "user/profile/public";
        } catch (Exception e) {
            return "redirect:/courses/";
        }
    }

    /**
     * Возвращает изображение профиля пользователя в виде массива байтов.
     *
     * @param filename имя файла изображения (включая расширение)
     * @return ResponseEntity с массивом байтов изображения и соответствующим Content-Type
     */
    @GetMapping("/profile/picture/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String filename) {
        try {
            byte[] imageBytes = userProfileService.getProfilePicture(filename);

            // Определяем Content-Type
            String contentType = "image/jpeg"; // по умолчанию
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}