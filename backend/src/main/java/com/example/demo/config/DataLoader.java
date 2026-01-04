package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            repository.save(new User("Alice", "alice@example.com", "alice", passwordEncoder.encode("password123")));
            repository.save(new User("Bob", "bob@example.com", "bob", passwordEncoder.encode("password123")));
            repository.save(new User("Charlie", "charlie@example.com", "charlie", passwordEncoder.encode("password123")));
            repository.save(new User("Admin", "admin@example.com", "admin", passwordEncoder.encode("admin123")));
        };
    }
}
