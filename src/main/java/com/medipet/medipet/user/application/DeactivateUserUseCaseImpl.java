package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.DeactivateUserUseCase;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DeactivateUserUseCaseImpl implements DeactivateUserUseCase {

    private final UserRepository userRepository;

    DeactivateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id.getValue()));
        user.setActive(false);
        userRepository.save(user);
    }
}
