package org.example.notificationservice.kafka;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.UserEventMessage;
import org.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class UserEventConsumer {

    private final EmailService emailService;
    @KafkaListener(topics = "user-events")
    public void handleUserEvent(UserEventMessage event) {
        log.info("Получено событие из Kafka: {}", event);

        if ("USER_CREATED".equals(event.getEventType())) {
            emailService.sendMessageCreated(event.getUserEmail(), event.getUserName());
        }
        else if ("USER_DELETED".equals(event.getEventType())) {
            emailService.sendMessageDelete(event.getUserEmail(), event.getUserName());
        }
        else {
            log.warn("Неизвестный тип события: {}", event.getEventType());
        }
    }


}
