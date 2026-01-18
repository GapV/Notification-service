package org.example.notificationservice.unit.kafka;

import org.example.notificationservice.dto.UserEventMessage;
import org.example.notificationservice.kafka.UserEventConsumer;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    void handleUserEvent_USER_CREATED() {

        UserEventMessage event = new UserEventMessage();
        event.setEventType("USER_CREATED");
        event.setUserEmail("test@example.com");
        event.setUserName("John Doe");

        userEventConsumer.handleUserEvent(event);

        verify(emailService, times(1))
                .sendMessageCreated("test@example.com", "John Doe");
    }

    @Test
    void handleUserEvent_USER_DELETED() {

        UserEventMessage event = new UserEventMessage();
        event.setEventType("USER_DELETED");
        event.setUserEmail("test@example.com");
        event.setUserName("Jane Doe");

        userEventConsumer.handleUserEvent(event);

        verify(emailService, times(1))
                .sendMessageDelete("test@example.com", "Jane Doe");
    }

    @Test
    void handleUserEvent_UnknownEventType() {

        UserEventMessage event = new UserEventMessage();
        event.setEventType("UNKNOWN_EVENT");
        event.setUserEmail("test@example.com");
        event.setUserName("John Doe");

        userEventConsumer.handleUserEvent(event);

        verify(emailService, never()).sendMessageCreated(any(), any());
        verify(emailService, never()).sendMessageDelete(any(), any());
    }
}
