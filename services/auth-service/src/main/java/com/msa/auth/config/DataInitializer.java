package com.msa.auth.config;

import com.msa.auth.model.ERole;
import com.msa.auth.model.Role;
import com.msa.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            log.info("Initial roles created: ROLE_USER, ROLE_ADMIN");
        } else {
            log.info("Roles already exist. Skipping initialization.");
        }
    }
}
