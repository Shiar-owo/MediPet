package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.ChangePasswordUseCase;
import com.medipet.medipet.user.domain.port.outbound.PasswordEncoder;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    ChangePasswordUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void execute(UserId id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id.getValue()));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
