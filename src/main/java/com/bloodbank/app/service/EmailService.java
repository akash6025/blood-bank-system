package com.bloodbank.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        System.out.println("=== EMAIL DEBUG INFO ===");
        System.out.println("From: " + fromEmail);
        System.out.println("To: " + toEmail);
        System.out.println("Token: " + resetToken);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Blood Bank - Password Reset Request");
        
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        
        String emailBody = "Hello,\n\n" +
                "You requested a password reset for your Blood Bank account.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour for security reasons.\n\n" +
                "If you didn't request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Blood Bank Management Team";
        
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Password reset email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
            System.err.println("❌ Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
