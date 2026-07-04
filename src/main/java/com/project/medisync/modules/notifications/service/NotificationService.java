package com.project.medisync.modules.notifications.service;

import com.project.medisync.modules.notifications.entity.Notification;
import com.project.medisync.modules.notifications.entity.TypeNotificationEnum;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du module Notifications.
 * Peut être appelée par n'importe quel autre module pour envoyer une notification.
 */
public interface NotificationService {

    Notification envoyer(UUID userId, TypeNotificationEnum type, String contenu);

    List<Notification> getByUser(UUID userId);

    List<Notification> getNonLuesByUser(UUID userId);

    long countNonLues(UUID userId);

    void marquerLue(UUID notificationId);

    void marquerToutesLues(UUID userId);
}
