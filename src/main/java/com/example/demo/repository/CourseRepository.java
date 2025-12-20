package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Репозиторий для работы с сущностью Course.
 * Предоставляет методы для выполнения операций с курсами в базе данных,
 * включая стандартные CRUD-операции, а также специализированные запросы для поиска, фильтрации и статистики.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Удаляет курс по его идентификатору.
     *
     * @param id идентификатор курса для удаления
     */
    void deleteCourseById(Long id);

    /**
     * Находит курс по его идентификатору.
     *
     * @param id идентификатор курса
     * @return найденный курс или null, если курс не существует
     */
    Course getCourseById(Long id);

    /**
     * Находит все курсы, созданные преподавателем с указанным идентификатором.
     *
     * @param teacherId идентификатор преподавателя
     * @return список курсов, созданных указанным преподавателем
     */
    List<Course> findCourseByTeacherId(Long teacherId);

    /**
     * Находит курсы, заголовок которых содержит указанную строку, без учета регистра.
     *
     * @param title строка для поиска в заголовке курса
     * @return список курсов, заголовок которых содержит указанную строку
     */
    List<Course> findByTitleContainingIgnoreCase(String title);

    /**
     * Находит все уникальные категории курсов.
     * Возвращает только те категории, которые не равны null.
     *
     * @return список уникальных категорий
     */
    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.category IS NOT NULL")
    List<String> findDistinctCategories();

    /**
     * Находит курсы, созданные преподавателем с указанным именем пользователя.
     *
     * @param teacherUsername имя пользователя преподавателя
     * @return список курсов, созданных указанным преподавателем
     */
    @Query("SELECT c FROM Course c WHERE c.teacher.username = :teacherUsername")
    List<Course> findByTeacherUsername(@Param("teacherUsername") String teacherUsername);

    /**
     * Подсчитывает количество курсов с указанной ценой.
     *
     * @param price для подсчета
     * @return количество курсов с указанной ценой
     */
    long countByPrice(BigDecimal price);

    /**
     * Подсчитывает количество курсов с ценой больше указанной.
     *
     * @param price для сравнения
     * @return количество курсов с ценой больше указанной
     */
    long countByPriceGreaterThan(BigDecimal price);

    /**
     * Вычисляет общий доход от платных курсов.
     * Суммирует произведение цены курса на количество записей для каждого платного курса.
     *
     * @return общий доход, если нет платных курсов, возвращает 0
     */
    @Query("SELECT COALESCE(SUM(c.price * SIZE(c.enrollments)), 0) FROM Course c WHERE c.price > 0")
    BigDecimal getTotalRevenue();

    /**
     * Вычисляет среднюю цену платных курсов.
     *
     * @return средняя цена платных курсов, если нет платных курсов, возвращает null
     */
    @Query("SELECT AVG(c.price) FROM Course c WHERE c.price > 0")
    BigDecimal getAverageCoursePrice();

    /**
     * Находит категории курсов и количество курсов в каждой категории.
     * Результат упорядочен по количеству курсов в убывающем порядке.
     *
     * @return список массивов объектов, где первый элемент - категория, второй - количество курсов
     */
    @Query("SELECT c.category, COUNT(c) as courseCount FROM Course c WHERE c.category IS NOT NULL GROUP BY c.category ORDER BY courseCount DESC")
    List<Object[]> findCategoriesWithCount();

    /**
     * Определяет самую популярную категорию курсов.
     * Использует метод findCategoriesWithCount и возвращает категорию с наибольшим количеством курсов.
     *
     * @return самая популярная категория или null, если нет курсов с категориями
     */
    default String findMostPopularCategory() {
        List<Object[]> results = findCategoriesWithCount();
        if (results.isEmpty()) {
            return null;
        }
        return (String) results.get(0)[0];
    }

    /**
     * Находит курсы, отсортированные по дате создания в убывающем порядке.
     * Использует пагинацию для ограничения количества результатов.
     *
     * @param pageable параметры пагинации
     * @return список курсов, отсортированных по дате создания (новые первыми)
     */
    List<Course> findTopByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Подсчитывает количество уникальных категорий курсов.
     * Учитывает только категории, которые не равны null.
     *
     * @return количество уникальных категорий
     */
    @Query("SELECT COUNT(DISTINCT c.category) FROM Course c WHERE c.category IS NOT NULL")
    long countDistinctCategories();

}