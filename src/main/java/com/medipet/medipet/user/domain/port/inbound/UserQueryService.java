package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Pageable;

public interface UserQueryService {

    User findById(UserId id);

    User findByEmail(String email);

    PageResponse<User> findAll(Pageable pageable);
}
