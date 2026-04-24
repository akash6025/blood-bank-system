package com.bloodbank.app.config;

import com.bloodbank.app.service.UserAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataRunner {

    @Bean
    CommandLineRunner seedAdmin(UserAccountService users) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (!users.exists(adminEmail)) {
                users.createUser(adminEmail, "Admin@123", "ROLE_ADMIN");
                System.out.println("Seeded default admin: " + adminEmail + " / Admin@123");
            }
        };
    }
}
