package com.project.medisync.modules.auth.service.impl;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.repository.UserRepository;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return userRepository.findById(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void delete(String id) {
        User user = getById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("[Auth] Utilisateur {} soft-deleted.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getMedecinsEtDelegues(RoleEnum role, String search, Pageable pageable) {
        return userRepository.findMedecinsEtDelegues(role, search, pageable);
    }

    @Override
    @Transactional
    public User activer(String id) {
        User user = getById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User desactiver(String id) {
        User user = getById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }
}
