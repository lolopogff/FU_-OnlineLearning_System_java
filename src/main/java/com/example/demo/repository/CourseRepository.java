package com.example.demo.repository;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    void deleteCourseById(Long id);
    Course getCourseById(Long id);
    List<Course> findCourseByTeacherId(Long teacherId);
    List<Course> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.category IS NOT NULL")
    List<String> findDistinctCategories();

    // Поиск по преподавателю
    @Query("SELECT c FROM Course c WHERE c.teacher.username = :teacherUsername")
    List<Course> findByTeacherUsername(@Param("teacherUsername") String teacherUsername);
}
