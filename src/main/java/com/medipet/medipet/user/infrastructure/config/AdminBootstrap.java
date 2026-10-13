package com.medipet.medipet.user.infrastructure.config;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.port.inbound.CreateUserUseCase;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final CreateUserUseCase createUserUseCase;
    private final String adminEmail;
    private final String adminPassword;

    AdminBootstrap(UserRepository userRepository,
                   CreateUserUseCase createUserUseCase,
                   @Value("${medipet.admin.email:}") String adminEmail,
                   @Value("${medipet.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            log.warn("Admin bootstrap skipped: MEDIPET_ADMIN_EMAIL / MEDIPET_ADMIN_PASSWORD are not set");
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin bootstrap skipped: user '{}' already exists", adminEmail);
            return;
        }

        createUserUseCase.execute(adminEmail, adminPassword, "Admin", "System", Role.ADMIN, null, null, null);
        log.info("Initial admin user created: {}", adminEmail);
    }
}
