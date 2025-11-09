package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller("customProfileController")
@RequestMapping("/user")
public class ProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;

    public ProfileController(UserProfileService userProfileService, UserService userService) {
        this.userProfileService = userProfileService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Principal principal, Authentication authentication, Model model) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());


            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            // Убедитесь, что hasProfilePicture правильно вычисляется
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());
            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));

            return "user/profile/view";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

//    @GetMapping("/profile")
//    public String viewProfile(Authentication authentication, Model model) {
//        try {
//            User user = (User) authentication.getPrincipal();
//            UserProfile profile = userProfileService.getUserProfile(user.getId());
//
//            model.addAttribute("user", user);
//            model.addAttribute("profile", profile);
//            model.addAttribute("hasProfilePicture",
//                    profile != null &&
//                            profile.getProfilePicture() != null &&
//                            !profile.getProfilePicture().trim().isEmpty());
//
//            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
//            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
//            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
//
//            return "user/profile/view";
//        } catch (Exception e) {
//            return "redirect:/courses/";
//        }
//    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Principal principal, Model model) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());

            return "user/profile/edit";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Principal principal,
                                @RequestParam String bio,
                                @RequestParam String phone,
                                @RequestParam String location,
                                @RequestParam(required = false) String dateOfBirth,
                                @RequestParam String website,
                                @RequestParam(value = "profilePicture", required = false) MultipartFile file, // Добавьте этот параметр
                                RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Обновляем текстовые данные
            userProfileService.updateUserProfile(
                    user.getId(),
                    bio,
                    phone,
                    location,
                    dateOfBirth != null && !dateOfBirth.isEmpty() ?
                            java.time.LocalDate.parse(dateOfBirth) : null,
                    website
            );

            // Если есть файл, сохраняем его
            if (file != null && !file.isEmpty()) {
                userProfileService.saveProfilePicture(user.getId(), file);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/user/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }

    @PostMapping("/profile/upload-picture")
    public String uploadProfilePicture(Principal principal,
                                       @RequestParam("profilePicture") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!file.isEmpty()) {
                userProfileService.saveProfilePicture(user.getId(), file);
                redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            }

            return "redirect:/user/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading picture: " + e.getMessage());
            return "redirect:/user/profile";
        }
    }

//    @GetMapping("/profile/picture/{filename:.+}")
//    @ResponseBody
//    public byte[] getProfilePicture(@PathVariable String filename) {
//        return userProfileService.getProfilePicture(filename);
//    }

    @GetMapping("/profile/public/{username}")
    public String viewPublicProfile(@PathVariable String username, Model model) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserProfile profile = userProfileService.getUserProfile(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            model.addAttribute("hasProfilePicture",
                    profile != null &&
                            profile.getProfilePicture() != null &&
                            !profile.getProfilePicture().trim().isEmpty());

            return "user/profile/public";
        } catch (Exception e) {
            return "redirect:/courses/";
        }
    }

    @GetMapping("/profile/picture/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String filename) {
        try {
            byte[] imageBytes = userProfileService.getProfilePicture(filename);

            // Определяем Content-Type
            String contentType = "image/jpeg"; // по умолчанию
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}




//package com.example.demo.controller;
//
//import com.example.demo.entity.User;
//import com.example.demo.entity.UserProfile;
//import com.example.demo.service.UserProfileService;
//import com.example.demo.service.UserService;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//        import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping("/user")
//public class UserProfileController { // Изменили имя класса
//
//    private final UserProfileService userProfileService;
//    private final UserService userService;
//
//    public UserProfileController(UserProfileService userProfileService, UserService userService) {
//        this.userProfileService = userProfileService;
//        this.userService = userService;
//    }
//
//    public ProfileController(UserProfileService userProfileService, UserService userService) {
//        this.userProfileService = userProfileService;
//        this.userService = userService;
//    }
//    // остальной код без изменений...
//    @GetMapping("/profile")
//    public String viewProfile(Authentication authentication, Model model) {
//        try {
//            User user = (User) authentication.getPrincipal();
//            UserProfile profile = userProfileService.getUserProfile(user.getId());
//
//            model.addAttribute("user", user);
//            model.addAttribute("profile", profile);
//            model.addAttribute("hasProfilePicture",
//                    profile != null &&
//                            profile.getProfilePicture() != null &&
//                            !profile.getProfilePicture().trim().isEmpty());
//
//            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
//            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
//            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
//
//            return "user/profile/view";
//        } catch (Exception e) {
//            return "redirect:/courses/";
//        }
//    }
//
//    @GetMapping("/profile/edit")
//    @PreAuthorize("isAuthenticated()")
//    public String editProfileForm(Authentication authentication, Model model) {
//        try {
//            User user = (User) authentication.getPrincipal();
//            UserProfile profile = userProfileService.getUserProfile(user.getId());
//
//            model.addAttribute("user", user);
//            model.addAttribute("profile", profile);
//            model.addAttribute("hasProfilePicture",
//                    profile != null &&
//                            profile.getProfilePicture() != null &&
//                            !profile.getProfilePicture().trim().isEmpty());
//
//            model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
//            model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
//            model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
//
//            return "user/profile/edit";
//        } catch (Exception e) {
//            return "redirect:/login";
//        }
//    }
//
//    @PostMapping("/profile/edit")
//    @PreAuthorize("isAuthenticated()")
//    public String updateProfile(Authentication authentication,
//                                @RequestParam String bio,
//                                @RequestParam String phone,
//                                @RequestParam String location,
//                                @RequestParam(required = false) String dateOfBirth,
//                                @RequestParam String website,
//                                @RequestParam(value = "profilePicture", required = false) MultipartFile file,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            User user = (User) authentication.getPrincipal();
//
//            userProfileService.updateUserProfile(
//                    user.getId(),
//                    bio,
//                    phone,
//                    location,
//                    dateOfBirth != null && !dateOfBirth.isEmpty() ?
//                            java.time.LocalDate.parse(dateOfBirth) : null,
//                    website
//            );
//
//            if (file != null && !file.isEmpty()) {
//                userProfileService.saveProfilePicture(user.getId(), file);
//            }
//
//            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
//            return "redirect:/user/profile";
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
//            return "redirect:/user/profile/edit";
//        }
//    }
//
//    @PostMapping("/profile/upload-picture")
//    @PreAuthorize("isAuthenticated()")
//    public String uploadProfilePicture(Authentication authentication,
//                                       @RequestParam("profilePicture") MultipartFile file,
//                                       RedirectAttributes redirectAttributes) {
//        try {
//            User user = (User) authentication.getPrincipal();
//
//            if (!file.isEmpty()) {
//                userProfileService.saveProfilePicture(user.getId(), file);
//                redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated successfully!");
//            } else {
//                redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
//            }
//
//            return "redirect:/user/profile";
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading picture: " + e.getMessage());
//            return "redirect:/user/profile";
//        }
//    }
//
//    @GetMapping("/profile/picture/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String filename) {
//        try {
//            byte[] imageBytes = userProfileService.getProfilePicture(filename);
//
//            String contentType = "image/jpeg";
//            if (filename.toLowerCase().endsWith(".png")) {
//                contentType = "image/png";
//            } else if (filename.toLowerCase().endsWith(".gif")) {
//                contentType = "image/gif";
//            } else if (filename.toLowerCase().endsWith(".webp")) {
//                contentType = "image/webp";
//            }
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .body(imageBytes);
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/profile/public/{username}")
//    public String viewPublicProfile(@PathVariable String username,
//                                    Authentication authentication,
//                                    Model model) {
//        try {
//            User user = userService.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            UserProfile profile = userProfileService.getUserProfile(user.getId());
//
//            model.addAttribute("user", user);
//            model.addAttribute("profile", profile);
//            model.addAttribute("hasProfilePicture",
//                    profile != null &&
//                            profile.getProfilePicture() != null &&
//                            !profile.getProfilePicture().trim().isEmpty());
//
//            if (authentication != null && authentication.isAuthenticated()) {
//                User currentUser = (User) authentication.getPrincipal();
//                model.addAttribute("currentUser", currentUser);
//                model.addAttribute("isTeacher", userService.hasRole(authentication, "TEACHER"));
//                model.addAttribute("isStudent", userService.hasRole(authentication, "STUDENT"));
//                model.addAttribute("isAdmin", userService.hasRole(authentication, "ADMIN"));
//            }
//
//            return "user/profile/public";
//        } catch (Exception e) {
//            return "redirect:/courses/";
//        }
//    }
//}