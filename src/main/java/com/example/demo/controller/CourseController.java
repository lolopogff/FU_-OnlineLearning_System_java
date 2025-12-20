package com.example.demo.controller;

import com.example.demo.dto.CourseDTO;
import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для управления курсами в системе.
 * Обрабатывает запросы связанные с отображением, созданием, редактированием и удалением курсов.
 * Доступ к различным операциям контролируется ролями пользователей.
 */
@Controller
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

    /**
     * Сервис для работы с курсами.
     */
    CourseService courseService;

    /**
     * Сервис для работы с пользователями.
     */
    UserService userService;

    /**
     * Отображает список курсов с возможностью фильтрации и пагинации.
     * Поддерживает фильтрацию по поисковому запросу, категории, преподавателю, цене.
     *
     * @param search поисковый запрос по названию или описанию курса (опционально)
     * @param category категория курса для фильтрации (опционально)
     * @param teacher имя преподавателя для фильтрации (опционально)
     * @param minPriceStr минимальная цена курса в виде строки (опционально)
     * @param maxPriceStr максимальная цена курса в виде строки (опционально)
     * @param page номер текущей страницы для пагинации (по умолчанию 1)
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для получения информации о текущем пользователе
     * @return имя шаблона для отображения списка курсов "course/courses"
     */
    @RequestMapping("/")
    public String listOfCourses(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "teacher", required = false) String teacher,
                                @RequestParam(value = "minPrice", required = false) String minPriceStr,
                                @RequestParam(value = "maxPrice", required = false) String maxPriceStr,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                Model model, Authentication authentication) {

        List<Course> courses;
        List<String> categories = new ArrayList<>();
        List<User> teachers = new ArrayList<>();
        int pageSize = 9;

        try {
            // Конвертируем цену из String в BigDecimal
            BigDecimal minPrice = null;
            BigDecimal maxPrice = null;
            if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }

            // Получаем все курсы с фильтрами
            List<Course> allCourses = courseService.findCoursesWithFilters(search, category, teacher, minPrice, maxPrice);
            int totalCourses = allCourses.size();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

            // Корректируем номер страницы если нужно
            page = Math.max(1, Math.min(page, totalPages));

            // Пагинация
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCourses);
            courses = allCourses.subList(startIndex, endIndex);

            // Получаем уникальные категории для фильтра
            categories = courseService.getAllCategories();
            // Получаем преподавателей для фильтра
            teachers = userService.getAllTeachers();

            model.addAttribute("totalCourses", totalCourses);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);

        } catch (Exception e) {
            // В случае ошибки возвращаем пустые списки
            courses = new ArrayList<>();
            model.addAttribute("totalCourses", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
        }

        model.addAttribute("courses", courses != null ? courses : new ArrayList<>());
        model.addAttribute("categories", categories);
        model.addAttribute("teachers", teachers);
        model.addAttribute("user", authentication);
        model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
        model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
        model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
        return "course/courses";
    }

    /**
     * Отображает форму для создания нового курса.
     * Доступно только для пользователей с ролью TEACHER или ADMIN.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона для создания нового курса "course/new"
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @RequestMapping("/new")
    public String newCourse(Model model) {
        Course course = new Course();
        model.addAttribute("course", course);
        return "course/new";
    }

    /**
     * Сохраняет новый курс или обновляет существующий.
     *
     * @param course объект Course с данными из формы
     * @param auth объект Authentication для идентификации текущего пользователя
     * @return перенаправление на страницу со списком курсов
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCourse(@ModelAttribute("course") Course course, Authentication auth) {
        courseService.save(course, auth);
        return "redirect:/courses/";
    }

    /**
     * Отображает форму для редактирования существующего курса.
     * Доступно только для преподавателя, создавшего курс, или администратора.
     *
     * @param id идентификатор курса для редактирования
     * @param principal объект Principal для проверки прав доступа
     * @return ModelAndView с формой редактирования курса или перенаправлением
     */
    @RequestMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ModelAndView editCourse(@PathVariable Long id, Principal principal) {
        ModelAndView mav = new ModelAndView("course/edit");
        Course course = courseService.getCourseById(id);
        if (course == null) {
            mav.setViewName("redirect:/courses/");
            return mav;
        }
        if (!principal.getName().equals(course.getTeacher().getUsername())) {
            mav.setViewName("redirect:/courses/");
            return mav;
        }
        mav.addObject("course", new CourseDTO(course));
        return mav;
    }

    /**
     * Удаляет курс по идентификатору.
     * Доступно только для преподавателя, создавшего курс, или администратора.
     *
     * @param id идентификатор курса для удаления
     * @param authentication объект Authentication для проверки прав доступа
     * @return перенаправление на страницу со списком курсов
     */
    @RequestMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String deleteCourse(@PathVariable("id") Long id, Authentication authentication) {
        courseService.deleteCourse(id, authentication);
        return "redirect:/courses/";
    }

    /**
     * Отображает курсы текущего пользователя.
     * Для преподавателя отображает созданные им курсы, для студента - курсы на которые он записан.
     *
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для получения текущего пользователя
     * @return имя шаблона для отображения курсов пользователя "course/myCourses"
     */
    @GetMapping("/myCourses")
    public String myCourses(Model model, Authentication authentication) {
        try {
            List<Course> myCourses = courseService.getCourseByUserId(authentication);
            model.addAttribute("my_courses", myCourses);
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
            model.addAttribute("user", authentication);
            return "course/myCourses";
        } catch (Exception e) {
            // Логируем ошибку для диагностики
            System.err.println("Error in myCourses: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading courses: " + e.getMessage());
            return "course/myCourses";
        }
    }

    /**
     * Отображает детальную информацию о курсе.
     *
     * @param id идентификатор курса
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для получения текущего пользователя
     * @return имя шаблона для отображения деталей курса "course/course-details"
     */
    @GetMapping("/details/{id}")
    public String courseDetails(@PathVariable Long id, Model model, Authentication authentication) {
        // Добавляем информацию о текущем пользователе
        String currentUsername = null;
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            model.addAttribute("currentUser", user);
            currentUsername = user.getUsername();
        }

        // Добавляем имя текущего пользователя в модель
        model.addAttribute("currentUsername", currentUsername);

        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return "redirect:/courses/";
            }

            model.addAttribute("course", course);
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
            return "course/course-details";
        } catch (Exception e) {
            return "redirect:/courses/";
        }
    }

    /**
     * Отображает страницу информации об авторе/разработчике системы.
     *
     * @param model объект Model для передачи данных в представление
     * @param authentication объект Authentication для получения текущего пользователя
     * @return имя шаблона страницы об авторе "about/author"
     */
    @GetMapping("/about/author")
    public String aboutAuthor(Model model, Authentication authentication) {
        // Добавляем роли для согласованности с другими страницами
        if (authentication != null) {
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
            model.addAttribute("user", authentication.getPrincipal());
        }
        return "about/author";
    }
}