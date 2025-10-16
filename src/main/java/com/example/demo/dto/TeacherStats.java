package com.example.demo.dto;

// TeacherStats.java
public class TeacherStats {
    private String username;
    private Long courseCount;
    private Long enrollmentCount;

    // Конструкторы, геттеры и сеттеры
    public TeacherStats() {}

    public TeacherStats(String username, Long courseCount, Long enrollmentCount) {
        this.username = username;
        this.courseCount = courseCount;
        this.enrollmentCount = enrollmentCount;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getCourseCount() { return courseCount; }
    public void setCourseCount(Long courseCount) { this.courseCount = courseCount; }

    public Long getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(Long enrollmentCount) { this.enrollmentCount = enrollmentCount; }
}