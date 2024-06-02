package com.meal.RISE.Service;

import com.meal.RISE.Entity.Notification;
import com.meal.RISE.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(Notification notification) {
        // Business logic to send and save notification
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void triggerBookingNotification(Long id, String bookingMessage) {
    }

    public void triggerCancellationNotification(Long id, String cancellationMessage) {

    }

    public void sendNotification(Long employeeId, String message) {
    }
}