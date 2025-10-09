package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

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
    public String saveCourse(@ModelAttribute("course") Course course) {
        courseService.save(course);
        return "redirect:/courses/list";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView editCourse(@PathVariable Long id, Model model) {
        ModelAndView mav = new ModelAndView("course/edit");
        Course course = courseService.getCourseById(id);
        mav.addObject("course", course);
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses/list";
    }
}
