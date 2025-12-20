package com.example.demo.repository;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Enrollment (записи на курсы).
 * Предоставляет методы для выполнения операций с записями на курсы в базе данных,
 * включая стандартные CRUD-операции и специализированные запросы.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Находит запись на курс по ее идентификатору.
     *
     * @param id идентификатор записи на курс
     * @return найденная запись или null, если запись не существует
     */
    Enrollment findEnrollmentById(Long id);

    /**
     * Находит все записи на курсы для указанного пользователя (студента).
     *
     * @param user пользователь (студент)
     * @return список записей на курсы для указанного пользователя
     */
    List<Enrollment> findByStudent(User user);

    /**
     * Проверяет, существует ли запись на курс для указанного студента и курса.
     *
     * @param id  идентификатор студента
     * @param id1 идентификатор курса
     * @return true, если запись существует, иначе false
     */
    boolean existsByStudentIdAndCourseId(Long id, Long id1);
}