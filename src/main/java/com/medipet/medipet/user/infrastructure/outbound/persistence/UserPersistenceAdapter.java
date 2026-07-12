package com.medipet.medipet.user.infrastructure.outbound.persistence;

import com.medipet.medipet.user.infrastructure.outbound.persistence.entity.UserEntity;
import com.medipet.medipet.user.infrastructure.outbound.persistence.mapper.UserEntityMapper;
import com.medipet.medipet.user.infrastructure.outbound.persistence.repository.UserJpaRepository;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.getValue())
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(UserEntityMapper::toDomain);
    }

    @Override
    public Page<User> findByRole(Role role, Pageable pageable) {
        return jpaRepository.findByRole(role.name(), pageable).map(UserEntityMapper::toDomain);
    }

    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<User> findAllById(Collection<UserId> ids) {
        return jpaRepository.findAllById(ids.stream().map(UserId::getValue).toList())
                .stream()
                .map(UserEntityMapper::toDomain)
                .toList();
    }
}
