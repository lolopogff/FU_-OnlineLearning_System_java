package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.getCourseById(id);
    }

    public void save(Course course, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        course.setTeacher(user);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.getCourseById(courseId);

        if (user.getId().equals(course.getTeacher().getId())) {
            courseRepository.deleteCourseById(courseId);
        }
        else{
            throw new RuntimeException("You can't delete this course");
        }
    }

    public List<Course> getCourseByUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ArrayList<>();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole().equals("TEACHER")) {
            return courseRepository.findCourseByTeacherId(user.getId());
        }
        return new ArrayList<>();
    }
}
