package com.bloodbank.app.service;

import com.bloodbank.app.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {
    
    private final UserAccountService userAccountService;
    private final EmailService emailService;
    private final Map<String, String> resetTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    
    public PasswordResetService(UserAccountService userAccountService, EmailService emailService) {
        this.userAccountService = userAccountService;
        this.emailService = emailService;
    }
    
    public boolean requestPasswordReset(String email) {
        UserAccount user = userAccountService.findByEmail(email);
        if (user == null) {
            return false;
        }
        
        // Generate reset token
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, email);
        tokenExpiry.put(token, System.currentTimeMillis() + 3600000); // 1 hour expiry
        
        // Send password reset email
        try {
            emailService.sendPasswordResetEmail(email, token);
            System.out.println("Password reset email sent to: " + email + " with token: " + token);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean validateResetToken(String token) {
        if (!resetTokens.containsKey(token)) {
            return false;
        }
        
        // Check if token is expired
        Long expiry = tokenExpiry.get(token);
        if (expiry == null || System.currentTimeMillis() > expiry) {
            resetTokens.remove(token);
            tokenExpiry.remove(token);
            return false;
        }
        
        return true;
    }
    
    public boolean resetPassword(String token, String newPassword) {
        if (!validateResetToken(token)) {
            return false;
        }
        
        String email = resetTokens.get(token);
        UserAccount user = userAccountService.findByEmail(email);
        
        if (user != null) {
            userAccountService.updatePassword(user, newPassword);
            // Clean up token
            resetTokens.remove(token);
            tokenExpiry.remove(token);
            return true;
        }
        
        return false;
    }
    
    // For demo purposes - get the reset token for an email
    public String getResetTokenForEmail(String email) {
        return resetTokens.entrySet().stream()
            .filter(entry -> entry.getValue().equals(email))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
}
