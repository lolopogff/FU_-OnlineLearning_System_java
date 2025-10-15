package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/myCourses")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public String getAllEnrollments(Model model, Authentication authentication) {
        model.addAttribute("enrollments", enrollmentService.getAllUserEnrollments(authentication));
        model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
        model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
        return "enrollments/myEnrollments";
    }

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