package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

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

    // Новые методы для статистики
    long countByPrice(BigDecimal price);

    long countByPriceGreaterThan(BigDecimal price);

    @Query("SELECT COALESCE(SUM(c.price * SIZE(c.enrollments)), 0) FROM Course c WHERE c.price > 0")
    BigDecimal getTotalRevenue();

    @Query("SELECT AVG(c.price) FROM Course c WHERE c.price > 0")
    BigDecimal getAverageCoursePrice();

    @Query("SELECT c.category, COUNT(c) as courseCount FROM Course c WHERE c.category IS NOT NULL GROUP BY c.category ORDER BY courseCount DESC")
    List<Object[]> findCategoriesWithCount();

    default String findMostPopularCategory() {
        List<Object[]> results = findCategoriesWithCount();
        if (results.isEmpty()) {
            return null;
        }
        return (String) results.get(0)[0];
    }

    List<Course> findTopByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c.category) FROM Course c WHERE c.category IS NOT NULL")
    long countDistinctCategories();

}
