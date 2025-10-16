package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateEnrollmentException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseRepository courseRepository;


    public List<Enrollment> getAllUserEnrollments(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        if (user.getRole().equals("STUDENT")) {
            return enrollmentRepository.findByStudent(user);
        }
        else {
            return new ArrayList<Enrollment>();
        }
    }


    public Enrollment enrollStudent(Enrollment enrollment, Authentication authentication) throws Exception {
        // 1. Находим пользователя
        User student = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Проверяем, что пользователь - студент (дополнительная валидация)
        if (!student.getRole().equals("STUDENT")) {
            throw new AccessDeniedException("Only students can enroll in courses");
        }

        // 3. Проверяем существование курса
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        // 4. Проверяем, не записан ли уже студент на этот курс
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course");
        }

        // 5. Устанавливаем студента и курс
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        // 6. Сохраняем запись
        return enrollmentRepository.save(enrollment);
    }

    public void unenrollStudent(Long enrollmentId, Authentication authentication) throws Exception {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(enrollmentId);
        if (!enrollment.getStudent().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only students can unenroll in courses");
        }
        enrollmentRepository.delete(enrollment);
    }

    public long getTotalEnrollmentsCount() {
        return enrollmentRepository.count();
    }

    public long getRecentEnrollmentsCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return enrollmentRepository.countByEnrolledAtAfter(since);
    }



}

