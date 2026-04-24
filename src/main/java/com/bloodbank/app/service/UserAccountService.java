package com.bloodbank.app.service;

import com.bloodbank.app.model.UserAccount;
import com.bloodbank.app.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserAccountService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);
    
    private final UserAccountRepository repo;
    private final PasswordEncoder encoder;

    public UserAccountService(UserAccountRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("User login attempt: {}", email);
        UserAccount ua = repo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
        GrantedAuthority authority = new SimpleGrantedAuthority(ua.getRole());
        logger.info("User logged in successfully: {} with role: {}", email, ua.getRole());
        return new User(ua.getEmail(), ua.getPassword(), ua.isEnabled(), true, true, true,
                Collections.singleton(authority));
    }

    public boolean exists(String email) { return repo.existsByEmail(email); }
    
    public UserAccount findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }
    
    public void updatePassword(UserAccount user, String newPassword) {
        logger.info("Updating password for user: {}", user.getEmail());
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
        logger.info("Password updated successfully for user: {}", user.getEmail());
    }

    public UserAccount createUser(String email, String rawPassword, String role) {
        logger.info("Creating new user account: {} with role: {}", email, role);
        UserAccount ua = new UserAccount();
        ua.setEmail(email);
        ua.setPassword(encoder.encode(rawPassword));
        ua.setRole(role);
        ua.setEnabled(true);
        UserAccount savedUser = repo.save(ua);
        logger.info("Successfully created user account: {} with ID: {}", email, savedUser.getId());
        return savedUser;
    }
}
