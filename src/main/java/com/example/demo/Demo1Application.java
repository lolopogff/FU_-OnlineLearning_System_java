package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения Spring Boot.
 * Содержит точку входа в приложение и конфигурацию Spring Boot.
 *
 * <p>Аннотация {@link SpringBootApplication} объединяет три аннотации:
 * <ul>
 *   <li>{@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} - включает автоматическую конфигурацию Spring Boot</li>
 *   <li>{@link org.springframework.context.annotation.ComponentScan} - включает сканирование компонентов в текущем пакете и его подпакетах</li>
 *   <li>{@link org.springframework.boot.autoconfigure.SpringBootApplication} - указывает, что это класс конфигурации Spring Boot</li>
 * </ul>
 */
@SpringBootApplication
public class Demo1Application {

    /**
     * Точка входа в приложение.
     * Запускает Spring Boot приложение с указанными аргументами командной строки.
     *
     * @param args аргументы командной строки, передаваемые при запуске приложения
     */
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}