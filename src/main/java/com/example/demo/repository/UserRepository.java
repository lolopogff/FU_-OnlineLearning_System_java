package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findById(long id);

    List<User> findByRole(String role);

    long countByRole(String role);

    long countByCreatedAtAfter(LocalDateTime since);


    @Query("SELECT u.username, COUNT(c), COALESCE(SUM(SIZE(c.enrollments)), 0) " +
            "FROM User u " +
            "LEFT JOIN u.taughtCourses c " +
            "WHERE u.role = 'TEACHER' " +
            "GROUP BY u.id, u.username " +
            "ORDER BY COUNT(c) DESC, COALESCE(SUM(SIZE(c.enrollments)), 0) DESC")
    List<Object[]> findTopTeachersWithStats(int limit);
}
