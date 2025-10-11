package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    CourseService courseService;


    @RequestMapping("/")
    public String listOfCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
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
        model.addAttribute("my_courses", courseService.getCourseByUserId(authentication));
        return "course/myCourses";
    }

}
