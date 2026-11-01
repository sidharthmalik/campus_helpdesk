package com.campus.helpdesk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final HelpdeskRepository repo;
    private final String username;
    private final String password;

    public AdminSeeder(HelpdeskRepository repo,
                       @Value("${app.admin.username:admin}") String username,
                       @Value("${app.admin.password:admin123}") String password) {
        this.repo = repo;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (repo.countAdmins() == 0) {
            repo.createAdmin(username, new BCryptPasswordEncoder().encode(password));
            System.out.println("Default admin created: " + username);
        }
    }
}
