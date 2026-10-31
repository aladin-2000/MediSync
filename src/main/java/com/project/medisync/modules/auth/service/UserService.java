package com.project.medisync.modules.auth.service;

import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.entity.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User getById(String id);

    User getByEmail(String email);

    User save(User user);

    List<User> saveAll(List<User> users);

    boolean existsById(String id);

    boolean existsByEmail(String email);

    void delete(String id);

    Page<User> getMedecinsEtDelegues(RoleEnum role, String search, Pageable pageable);

    User activer(String id);

    User desactiver(String id);
}
