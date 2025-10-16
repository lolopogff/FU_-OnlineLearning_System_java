package com.example.demo.service;

import com.example.demo.dto.TeacherStats;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getCurrentUser(Authentication authentication) {
        return findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllTeachers() {
        return userRepository.findByRole("TEACHER");
    }

    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public Long getTotalUsersCount(){
        return userRepository.count();
    }

    public long getUsersCountByRole(String role) {
        return userRepository.countByRole(role);
    }

    public long getNewUsersCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.countByCreatedAtAfter(since);
    }

    public List<TeacherStats> getTopTeachers(int limit) {
        List<Object[]> results = userRepository.findTopTeachersWithStats(limit);
        return results.stream().map(result -> {
            TeacherStats stats = new TeacherStats();
            stats.setUsername((String) result[0]);
            stats.setCourseCount((Long) result[1]);
            stats.setEnrollmentCount((Long) result[2]);
            return stats;
        }).collect(Collectors.toList());
    }
}