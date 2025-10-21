package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }
    public void save(Course course, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
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

        if (user.getId().equals(course.getTeacher().getId()) || user.getRole().equals("ADMIN")) {
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

    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Course> findCoursesWithFilters(String search, String category, String teacher,
                                               BigDecimal minPrice, BigDecimal maxPrice) {
        // Если нет фильтров, возвращаем все курсы
        if (search == null && category == null && teacher == null && minPrice == null && maxPrice == null) {
            return courseRepository.findAll();
        }

        // Постепенно применяем фильтры
        List<Course> courses = courseRepository.findAll();

        // Фильтрация по поиску
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            courses = courses.stream()
                    .filter(course -> course.getTitle().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Фильтрация по категории
        if (category != null && !category.trim().isEmpty()) {
            courses = courses.stream()
                    .filter(course -> category.equals(course.getCategory()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по преподавателю
        if (teacher != null && !teacher.trim().isEmpty()) {
            courses = courses.stream()
                    .filter(course -> course.getTeacher() != null && teacher.equals(course.getTeacher().getUsername()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по минимальной цене
        if (minPrice != null) {
            courses = courses.stream()
                    .filter(course -> course.getPrice() != null && course.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        // Фильтрация по максимальной цене
        if (maxPrice != null) {
            courses = courses.stream()
                    .filter(course -> course.getPrice() != null && course.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return courses;
    }

    public List<String> getAllCategories() {
        List<Course> allCourses = courseRepository.findAll();
        return allCourses.stream()
                .map(Course::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public Long getTotalCoursesCount(){
        return courseRepository.count();
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = courseRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public long getFreeCoursesCount() {
        return courseRepository.countByPrice(BigDecimal.ZERO);
    }

    public long getPaidCoursesCount() {
        return courseRepository.countByPriceGreaterThan(BigDecimal.ZERO);
    }

    public BigDecimal getAverageCoursePrice() {
        BigDecimal avgPrice = courseRepository.getAverageCoursePrice();
        return avgPrice != null ? avgPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public String getMostPopularCategory() {
        return courseRepository.findMostPopularCategory();
    }

    public List<Course> getRecentCourses(int limit) {
        return courseRepository.findTopByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    public long getTotalCategoriesCount() {
        return courseRepository.countDistinctCategories();
    }
}
