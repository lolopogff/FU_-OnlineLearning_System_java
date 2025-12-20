package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для управления записями на курсы (Enrollment).
 * Обрабатывает операции связанные с записью, отменой записи и просмотром записей пользователя.
 */
@Controller
@RequestMapping("/myCourses")
@AllArgsConstructor
public class EnrollmentController {

    /**
     * Сервис для работы с записями на курсы.
     */
    private EnrollmentService enrollmentService;

    /**
     * Сервис для работы с пользователями.
     */
    private UserService userService;

    /**
     * Отображает все записи текущего пользователя на курсы.
     *
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для получения текущего пользователя
     * @return имя шаблона для отображения записей пользователя "enrollments/myEnrollments"
     */
    @GetMapping("/all")
    public String getAllEnrollments(Model model, Authentication authentication) {
        model.addAttribute("enrollments", enrollmentService.getAllUserEnrollments(authentication));
        model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
        model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
        model.addAttribute("user", authentication);
        return "enrollments/myEnrollments";
    }

    /**
     * Отображает форму для записи на курс.
     * Доступно только для пользователей с ролью STUDENT.
     *
     * @param courseId идентификатор курса, на который происходит запись
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для проверки роли пользователя
     * @return имя шаблона формы записи "enrollments/enrollForm" или перенаправление на список курсов
     */
    @GetMapping("/enroll")
    public String showEnrollForm(@RequestParam Long courseId, Model model, Authentication authentication) {
        // Проверяем, что пользователь - студент
        if (!userService.hasRole(authentication, "STUDENT")) {
            return "redirect:/courses/";
        }

        Enrollment enrollment = new Enrollment();
        model.addAttribute("enrollment", enrollment);
        model.addAttribute("courseId", courseId);
        return "enrollments/enrollForm";
    }

    /**
     * Обрабатывает запись студента на курс.
     * Создает новую запись на курс для текущего пользователя.
     *
     * @param enrollment объект Enrollment с данными записи
     * @param authentication объект Authentication для идентификации текущего пользователя
     * @param redirectAttributes атрибуты для передачи сообщений при перенаправлении
     * @return перенаправление на страницу с записями пользователя или список курсов
     */
    @PostMapping("/enrollWithObject")
    public String enrollStudent(Enrollment enrollment,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Enrollment savedEnrollment = enrollmentService.enrollStudent(enrollment, authentication);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully enrolled in the course!");
            return "redirect:/myCourses/all";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to enroll in course: " + e.getMessage());
            return "redirect:/courses";
        }
    }

    /**
     * Обрабатывает отмену записи на курс.
     * Удаляет существующую запись пользователя на курс.
     *
     * @param enrollmentId идентификатор записи для отмены
     * @param authentication объект Authentication для проверки прав доступа
     * @param redirectAttributes атрибуты для передачи сообщений при перенаправлении
     * @return перенаправление на страницу с записями пользователя
     */
    @PostMapping("/unenroll")
    public String unenrollFromCourse(@RequestParam Long enrollmentId,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.unenrollStudent(enrollmentId, authentication);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully unenrolled from the course!");
            return "redirect:/myCourses/all";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to unenroll from course: " + e.getMessage());
            return "redirect:/myCourses/all";
        }
    }
}