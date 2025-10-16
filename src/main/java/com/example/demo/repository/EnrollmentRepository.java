package com.example.demo.repository;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findEnrollmentById(Long id);
    List<Enrollment> findByStudent(User user);
    List<Enrollment> findByCourse(Course course);
    boolean existsByStudentIdAndCourseId(Long id, Long id1);

    long countByEnrolledAtAfter(LocalDateTime since);
}
