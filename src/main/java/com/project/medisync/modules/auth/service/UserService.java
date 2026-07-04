package com.project.medisync.modules.auth.service;

import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.entity.RoleEnum;

import java.util.UUID;

/**
 * Interface publique du module Auth.
 * Les autres modules passent obligatoirement par cette interface
 * pour accéder aux données utilisateur — jamais par UserRepository directement.
 */
public interface UserService {

    User getById(UUID id);

    User getByEmail(String email);

    User save(User user);

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    /** Soft delete de l'utilisateur. */
    void delete(UUID id);
}
