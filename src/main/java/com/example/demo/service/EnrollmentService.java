package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateEnrollmentException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления записями на курсы (Enrollment).
 * Обеспечивает бизнес-логику для операций записи студентов на курсы,
 * отмены записей и получения информации о записях пользователей.
 */
@Service
@Transactional
@AllArgsConstructor
public class EnrollmentService {

    /**
     * Репозиторий для работы с записями на курсы.
     */
    private  EnrollmentRepository enrollmentRepository;

    /**
     * Сервис для работы с пользователями.
     */
    private UserService userService;

    /**
     * Репозиторий для работы с курсами.
     */
    private CourseRepository courseRepository;

    /**
     * Возвращает все записи на курсы для текущего пользователя.
     * Для пользователей с ролью STUDENT возвращает список курсов, на которые они записаны.
     * Для пользователей с другими ролями возвращает пустой список.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return список записей на курсы для текущего пользователя или пустой список
     */
    public List<Enrollment> getAllUserEnrollments(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        if (user.getRole().equals("STUDENT")) {
            return enrollmentRepository.findByStudent(user);
        }
        else {
            return new ArrayList<Enrollment>();
        }
    }

    /**
     * Записывает студента на курс.
     * Выполняет следующие проверки:
     * 1. Существование пользователя
     * 2. Роль пользователя (только STUDENT может записываться на курсы)
     * 3. Существование курса
     * 4. Отсутствие дублирующей записи (студент не должен быть уже записан на этот курс)
     *
     * @param enrollment объект записи на курс (должен содержать идентификатор курса)
     * @param authentication объект аутентификации текущего пользователя
     * @return сохраненная запись на курс
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если пользователь не имеет роли STUDENT
     * @throws EntityNotFoundException если курс не найден
     * @throws DuplicateEnrollmentException если студент уже записан на этот курс
     */
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

    /**
     * Отменяет запись студента на курс.
     * Проверяет, что отменить запись может только тот студент, который записан на курс.
     *
     * @param enrollmentId идентификатор записи на курс для отмены
     * @param authentication объект аутентификации текущего пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если текущий пользователь не является студентом, записанным на курс
     */
    public void unenrollStudent(Long enrollmentId, Authentication authentication) throws Exception {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Enrollment enrollment = enrollmentRepository.findEnrollmentById(enrollmentId);
        if (!enrollment.getStudent().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only students can unenroll in courses");
        }
        enrollmentRepository.delete(enrollment);
    }

    /**
     * Возвращает общее количество записей на курсы в системе.
     *
     * @return общее количество записей на курсы
     */
    public long getTotalEnrollmentsCount() {
        return enrollmentRepository.count();
    }
}