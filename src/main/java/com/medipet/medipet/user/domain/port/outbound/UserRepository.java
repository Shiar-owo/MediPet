package com.medipet.medipet.user.domain.port.outbound;

import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAll(Pageable pageable);

    void deleteById(UserId id);
}
