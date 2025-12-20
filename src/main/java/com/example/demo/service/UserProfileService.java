package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сервис для управления профилями пользователей.
 * Обеспечивает бизнес-логику для операций с профилями пользователей,
 * включая обновление информации, загрузку изображений профиля и работу с файловой системой.
 * Использует транзакционность для обеспечения целостности данных.
 */
@Service
@Transactional
public class UserProfileService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий для работы с профилями пользователей.
     */
    private final UserProfileRepository userProfileRepository;

    /**
     * Корневая директория для хранения загруженных изображений профилей.
     */
    private final Path rootLocation = Paths.get("uploads/profile-pictures");

    /**
     * Конструктор сервиса профилей пользователей.
     * Инициализирует репозитории и создает директорию для хранения изображений профилей.
     *
     * @param userRepository репозиторий для работы с пользователями
     * @param userProfileRepository репозиторий для работы с профилями пользователей
     * @throws RuntimeException если не удается создать директорию для хранения файлов
     */
    public UserProfileService(UserRepository userRepository,
                              UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;

        // Создаем директорию для загрузки файлов
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    /**
     * Получает профиль пользователя по идентификатору пользователя.
     * Если профиль не существует, создает новый профиль для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return профиль пользователя
     * @throws RuntimeException если пользователь с указанным идентификатором не найден
     */
    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

    /**
     * Обновляет информацию профиля пользователя.
     *
     * @param userId идентификатор пользователя
     * @param bio новая биография пользователя
     * @param phone новый номер телефона
     * @param location новое местоположение
     * @param dateOfBirth новая дата рождения
     * @param website новый веб-сайт
     */
    public void updateUserProfile(Long userId, String bio, String phone,
                                  String location, LocalDate dateOfBirth, String website) {
        UserProfile profile = getUserProfile(userId);
        profile.setBio(bio);
        profile.setPhone(phone);
        profile.setLocation(location);
        profile.setDateOfBirth(dateOfBirth);
        profile.setWebsite(website);

        userProfileRepository.save(profile);
    }

    /**
     * Сохраняет изображение профиля пользователя.
     * Генерирует уникальное имя файла для предотвращения конфликтов.
     *
     * @param userId идентификатор пользователя
     * @param file загружаемый файл изображения
     * @throws RuntimeException если файл пуст или произошла ошибка при сохранении
     */
    public void saveProfilePicture(Long userId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Генерируем уникальное имя файла
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            Path destinationFile = rootLocation.resolve(Paths.get(newFilename))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile);

            // Обновляем путь в профиле
            UserProfile profile = getUserProfile(userId);
            profile.setProfilePicture(newFilename);
            userProfileRepository.save(profile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    /**
     * Возвращает содержимое файла изображения профиля в виде массива байтов.
     *
     * @param filename имя файла изображения
     * @return массив байтов содержимого файла
     * @throws RuntimeException если произошла ошибка при чтении файла
     */
    public byte[] getProfilePicture(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    /**
     * Обновляет базовую информацию пользователя (имя и фамилия).
     * Выполняет валидацию входных данных.
     *
     * @param userId идентификатор пользователя
     * @param firstName новое имя пользователя
     * @param lastName новая фамилия пользователя
     * @throws RuntimeException если пользователь не найден или имя/фамилия пусты
     */
    public void updateUserBasicInfo(Long userId, String firstName, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usdate_of_birther not found"));

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new RuntimeException("Имя не может быть пустым");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new RuntimeException("Фамилия не может быть пустой");
        }

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        userRepository.save(user);
    }
}