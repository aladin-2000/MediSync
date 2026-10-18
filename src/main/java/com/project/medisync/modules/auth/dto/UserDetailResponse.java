package com.project.medisync.modules.auth.dto;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDetailResponse {

    private String id;
    private String email;
    private RoleEnum role;
    private Boolean isActive;
    private Boolean emailVerified;
    private String nom;
    private String prenom;
    private LocalDateTime createdAt;

    public static UserDetailResponse from(User user, String nom, String prenom) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .nom(nom)
                .prenom(prenom)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
