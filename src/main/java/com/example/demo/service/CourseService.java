package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

/**
 * Сервис для управления курсами в системе.
 * Предоставляет бизнес-логику для операций с курсами, включая создание, редактирование,
 * удаление, поиск и получение статистики.
 */
@Service
public class CourseService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private final CourseRepository courseRepository;

    /**
     * Репозиторий для работы с курсами.
     */
    private final UserRepository userRepository;

    /**
     * Конструктор сервиса курсов.
     *
     * @param userRepository репозиторий для работы с пользователями
     * @param courseRepository репозиторий для работы с курсами
     */
    public CourseService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Находит курс по его идентификатору.
     *
     * @param id идентификатор курса
     * @return найденный курс
     * @throws EntityNotFoundException если курс с указанным идентификатором не найден
     */
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    /**
     * Сохраняет курс и устанавливает текущего пользователя в качестве преподавателя.
     *
     * @param course курс для сохранения
     * @param auth объект аутентификации текущего пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public void save(Course course, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        course.setTeacher(user);
        courseRepository.save(course);
    }

    /**
     * Удаляет курс по идентификатору, если текущий пользователь является его преподавателем или администратором.
     *
     * @param courseId идентификатор курса для удаления
     * @param authentication объект аутентификации текущего пользователя
     * @throws RuntimeException если пользователь не найден или не имеет прав на удаление курса
     */
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

    /**
     * Возвращает курсы, связанные с текущим пользователем.
     * Для преподавателя возвращает курсы, которые он создал.
     * Для других ролей возвращает пустой список.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return список курсов пользователя или пустой список
     */
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

    /**
     * Находит курсы с применением указанных фильтров.
     * Фильтры применяются последовательно, если они заданы.
     *
     * @param search строка для поиска в названии курса
     * @param category категория курса
     * @param teacher имя пользователя преподавателя
     * @param minPrice минимальная цена курса
     * @param maxPrice максимальная цена курса
     * @return список курсов, соответствующих всем заданным фильтрам
     */
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

    /**
     * Возвращает список всех уникальных категорий курсов.
     *
     * @return список категорий
     */
    public List<String> getAllCategories() {
        List<Course> allCourses = courseRepository.findAll();
        return allCourses.stream()
                .map(Course::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Возвращает общее количество курсов в системе.
     *
     * @return количество курсов
     */
    public Long getTotalCoursesCount(){
        return courseRepository.count();
    }

    /**
     * Возвращает общий доход от всех платных курсов.
     *
     * @return общий доход или 0, если доход не определен
     */
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = courseRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    /**
     * Возвращает количество бесплатных курсов (с ценой 0).
     *
     * @return количество бесплатных курсов
     */
    public long getFreeCoursesCount() {
        return courseRepository.countByPrice(BigDecimal.ZERO);
    }

    /**
     * Возвращает количество платных курсов (с ценой больше 0).
     *
     * @return количество платных курсов
     */
    public long getPaidCoursesCount() {
        return courseRepository.countByPriceGreaterThan(BigDecimal.ZERO);
    }

    /**
     * Возвращает среднюю цену платных курсов.
     *
     * @return средняя цена платных курсов или 0, если нет платных курсов
     */
    public BigDecimal getAverageCoursePrice() {
        BigDecimal avgPrice = courseRepository.getAverageCoursePrice();
        return avgPrice != null ? avgPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * Возвращает самую популярную категорию курсов.
     *
     * @return название самой популярной категории или null, если категории отсутствуют
     */
    public String getMostPopularCategory() {
        return courseRepository.findMostPopularCategory();
    }

    /**
     * Возвращает список недавно созданных курсов.
     *
     * @param limit максимальное количество возвращаемых курсов
     * @return список недавних курсов
     */
    public List<Course> getRecentCourses(int limit) {
        return courseRepository.findTopByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    /**
     * Возвращает количество уникальных категорий курсов.
     *
     * @return количество категорий
     */
    public long getTotalCategoriesCount() {
        return courseRepository.countDistinctCategories();
    }
}