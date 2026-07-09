package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.UpdateUserUseCase;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;

    UpdateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User execute(UserId id, String firstName, String lastName, String role, String phone, String address, String avatar) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id.getValue()));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (role != null) user.setRole(Role.valueOf(role));
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        if (avatar != null) user.setAvatar(avatar);

        return userRepository.save(user);
    }
}
