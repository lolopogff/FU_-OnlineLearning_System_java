package com.example.demo.controller;

import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Контроллер для обработки административных запросов.
 * Предоставляет доступ к статистике и другим административным функциям.
 * Доступ к методам контроллера разрешен только пользователям с ролью ADMIN.
 */
@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    /**
     * Сервис для работы с пользователями.
     */
    private UserService userService;

    /**
     * Сервис для работы с курсами.
     */
    private CourseService courseService;

    /**
     * Сервис для работы с записями на курсы.
     */
    private EnrollmentService enrollmentService;

    /**
     * Обрабатывает запрос на отображение страницы статистики.
     * Собирает и отображает различную статистическую информацию о системе,
     * включая данные о пользователях, курсах, доходах и активности.
     *
     * Метод защищен аннотацией @PreAuthorize, которая проверяет наличие роли ADMIN
     * у текущего пользователя. В случае ошибки при загрузке данных отображает
     * страницу со значениями по умолчанию.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона представления "admin/statistics"
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        try {
            // Основная статистика
            model.addAttribute("totalUsers", userService.getTotalUsersCount());
            model.addAttribute("totalCourses", courseService.getTotalCoursesCount());
            model.addAttribute("totalEnrollments", enrollmentService.getTotalEnrollmentsCount());
            model.addAttribute("totalRevenue", courseService.getTotalRevenue());

            // Статистика по пользователям
            model.addAttribute("studentCount", userService.getUsersCountByRole("STUDENT"));
            model.addAttribute("teacherCount", userService.getUsersCountByRole("TEACHER"));
            model.addAttribute("adminCount", userService.getUsersCountByRole("ADMIN"));
            model.addAttribute("newUsersCount", userService.getNewUsersCount(30));

            // Статистика по курсам
            model.addAttribute("freeCoursesCount", courseService.getFreeCoursesCount());
            model.addAttribute("paidCoursesCount", courseService.getPaidCoursesCount());
            model.addAttribute("averageCoursePrice", courseService.getAverageCoursePrice());
            model.addAttribute("mostPopularCategory", courseService.getMostPopularCategory());

            // Детальная информация
            model.addAttribute("recentCourses", courseService.getRecentCourses(5));
            model.addAttribute("topTeachers", userService.getTopTeachers(5));
            model.addAttribute("totalCategories", courseService.getTotalCategoriesCount());

            // Системная информация
            model.addAttribute("currentTime", LocalDateTime.now());

        } catch (Exception e) {
            // Логируем ошибку
            System.err.println("Error loading statistics: " + e.getMessage());
            e.printStackTrace();

            // Устанавливаем значения по умолчанию
            setDefaultStatistics(model);
        }

        return "admin/statistics";
    }

    /**
     * Устанавливает значения статистики по умолчанию в модель.
     * Используется при возникновении ошибок при загрузке реальных данных.
     * Все числовые значения устанавливаются в 0, строковые - в "N/A",
     * списки - в пустые коллекции.
     *
     * @param model объект Model для установки атрибутов по умолчанию
     */
    private void setDefaultStatistics(Model model) {
        model.addAttribute("totalUsers", 0L);
        model.addAttribute("totalCourses", 0L);
        model.addAttribute("totalEnrollments", 0L);
        model.addAttribute("totalRevenue", BigDecimal.ZERO);
        model.addAttribute("studentCount", 0L);
        model.addAttribute("teacherCount", 0L);
        model.addAttribute("adminCount", 0L);
        model.addAttribute("newUsersCount", 0L);
        model.addAttribute("freeCoursesCount", 0L);
        model.addAttribute("paidCoursesCount", 0L);
        model.addAttribute("averageCoursePrice", BigDecimal.ZERO);
        model.addAttribute("mostPopularCategory", "N/A");
        model.addAttribute("recentCourses", new ArrayList<>());
        model.addAttribute("topTeachers", new ArrayList<>());
        model.addAttribute("totalCategories", 0L);
        model.addAttribute("currentTime", LocalDateTime.now());
    }
}