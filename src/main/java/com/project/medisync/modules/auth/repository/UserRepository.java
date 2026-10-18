package com.project.medisync.modules.auth.repository;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(String id);

    @Query("SELECT u FROM User u "
         + "WHERE u.role IN ('MEDECIN','DELEGUE') "
         + "AND (:role IS NULL OR u.role = :role) "
         + "AND (:search IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%',:search,'%'))) "
         + "ORDER BY u.createdAt DESC")
    Page<User> findMedecinsEtDelegues(@Param("role") RoleEnum role,
                                       @Param("search") String search,
                                       Pageable pageable);
}
