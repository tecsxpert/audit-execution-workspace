package com.internship.tool.config;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ensures a test user exists so /auth/login works even if seed migration was skipped or DB was reset.
 */
@Component
@Order(0)
public class DemoUserBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserBootstrap.class);

    private final UserRepository userRepository;

    public DemoUserBootstrap(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("demo").isPresent()) {
            log.debug("Demo user already present");
            return;
        }
        User u = new User();
        u.setUsername("demo");
        u.setPassword("demo");
        u.setRole("USER");
        userRepository.saveAndFlush(u);
        log.info("Inserted demo user (username=demo, password=demo) for local testing");
    }
}
