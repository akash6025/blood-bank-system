package com.bloodbank.app.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("error", "Resource Not Found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
    
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("error", "Runtime Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", "System Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
}
