package com.bloodbank.app.controller;

import com.bloodbank.app.service.UserAccountService;
import com.bloodbank.app.service.PasswordResetService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserAccountService users;
    private final PasswordResetService passwordResetService;

    public AuthController(UserAccountService users, PasswordResetService passwordResetService) {
        this.users = users;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    public static class SignupForm {
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Please provide a valid email address")
        public String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        public String password;
        
        @NotBlank(message = "Please confirm your password")
        public String confirmPassword;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        return "auth/signup-simple";
    }

    @PostMapping("/signup")
    public String signup(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Debug: Print form data
        System.out.println("Signup attempt - Email: " + email);
        System.out.println("Signup attempt - Password length: " + (password != null ? password.length() : 0));
        
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            return "auth/signup-simple";
        }
        if (password == null || password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long");
            return "auth/signup-simple";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "auth/signup-simple";
        }
        if (users.exists(email)) {
            model.addAttribute("error", "Email already exists");
            return "auth/signup-simple";
        }
        users.createUser(email, password, "ROLE_USER");
        return "redirect:/login";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String forgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            return "auth/forgot-password";
        }
        
        boolean success = passwordResetService.requestPasswordReset(email);
        
        if (success) {
            model.addAttribute("success", "Password reset instructions have been sent to your email. For demo purposes, the reset link would be: /reset-password?token=" + passwordResetService.getResetTokenForEmail(email));
        } else {
            model.addAttribute("error", "Email address not found in our system");
        }
        
        return "auth/forgot-password";
    }
    
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.validateResetToken(token)) {
            model.addAttribute("error", "Invalid or expired reset token");
            return "auth/reset-password";
        }
        
        model.addAttribute("token", token);
        return "auth/reset-password";
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (token == null || token.trim().isEmpty()) {
            model.addAttribute("error", "Invalid reset token");
            return "auth/reset-password";
        }
        
        if (newPassword == null || newPassword.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        
        boolean success = passwordResetService.resetPassword(token, newPassword);
        
        if (success) {
            return "redirect:/login?success=true";
        } else {
            model.addAttribute("error", "Failed to reset password. Please try again.");
        }
        
        return "auth/reset-password";
    }
}
