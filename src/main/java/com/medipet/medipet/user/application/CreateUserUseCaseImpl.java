package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.CreateUserUseCase;
import com.medipet.medipet.user.domain.port.outbound.PasswordEncoder;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    CreateUserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User execute(String email, String password, String firstName, String lastName, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already in use: " + email);
        }

        User user = User.create(email, passwordEncoder.encode(password), firstName, lastName, role);
        return userRepository.save(user);
    }
}
