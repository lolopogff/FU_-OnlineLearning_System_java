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

@Service
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final Path rootLocation = Paths.get("uploads/profile-pictures");

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

    public UserProfile getUserProfileByUsername(String username) {
        return userProfileRepository.findByUserUsername(username)
                .orElseGet(() -> {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

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

    public byte[] getProfilePicture(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public void checkUploadDirectory() {
        try {
            System.out.println("=== Upload Directory Info ===");
            System.out.println("Path: " + rootLocation.toAbsolutePath());
            System.out.println("Exists: " + Files.exists(rootLocation));
            System.out.println("Writable: " + Files.isWritable(rootLocation));

            if (Files.exists(rootLocation)) {
                Files.list(rootLocation).forEach(path -> {
                    System.out.println("File: " + path.getFileName());
                });
            }
            System.out.println("=============================");
        } catch (Exception e) {
            System.out.println("Error checking directory: " + e.getMessage());
        }
    }

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