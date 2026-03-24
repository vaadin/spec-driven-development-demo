package com.example.specdriven.security;

import com.example.specdriven.model.Role;
import com.example.specdriven.model.User;
import com.example.specdriven.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Alice Customer", "customer@test.com", Role.CUSTOMER,
                    passwordEncoder.encode("customer@test.com")));
            userRepository.save(new User("Bob Agent", "agent@test.com", Role.ADMIN,
                    passwordEncoder.encode("agent@test.com")));
            userRepository.save(new User("Carol Manager", "manager@test.com", Role.ADMIN,
                    passwordEncoder.encode("manager@test.com")));
        }
    }
}
