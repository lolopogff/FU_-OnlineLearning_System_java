package com.example.demo.exception;

/**
 * Исключение, выбрасываемое при попытке повторной записи на курс.
 * Используется для обработки ситуаций, когда пользователь пытается записаться на курс,
 * на который уже был ранее записан.
 */
public class DuplicateEnrollmentException extends RuntimeException {

    /**
     * Создает новое исключение с заданным сообщением об ошибке.
     *
     * @param message сообщение об ошибке, описывающее причину исключения
     */
    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}