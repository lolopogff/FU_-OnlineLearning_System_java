package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    UserService userService;

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
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @RequestMapping("/new")
    public String newCourse(Model model) {
        Course course = new Course();
        model.addAttribute("course", course);
        return "course/new";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCourse(@ModelAttribute("course") Course course, Authentication auth) {
        courseService.save(course, auth);
        return "redirect:/courses/";
    }

    @RequestMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ModelAndView editCourse(@PathVariable Long id, Model model) {
        ModelAndView mav = new ModelAndView("course/edit");
        Course course = courseService.getCourseById(id);
        mav.addObject("course", course);
        return mav;
    }

    @RequestMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String deleteCourse(@PathVariable("id") Long id, Authentication authentication) {
        courseService.deleteCourse(id, authentication);
        return "redirect:/courses/";
    }

    @GetMapping("/myCourses")
    public String myCourses(Model model, Authentication authentication) {
        try {
            List<Course> myCourses = courseService.getCourseByUserId(authentication);
            model.addAttribute("my_courses", myCourses);
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
            return "course/myCourses";
        } catch (Exception e) {
            // Логируем ошибку для диагностики
            System.err.println("Error in myCourses: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading courses: " + e.getMessage());
            return "course/myCourses";
        }
    }

    @GetMapping("/details/{id}")
    public String courseDetails(@PathVariable Long id, Model model, Authentication authentication) {
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


}
