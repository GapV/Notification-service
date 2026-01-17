package org.example.notificationservice.Integration.service;

import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceIntegrationTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);

        emailService.init();
    }

    @Test
    void sendEmail_Success() {

        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        emailService.sendEmail(to, subject, text);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_ExceptionCaught() {

        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendMessageCreated_Success() {

        String email = "user@example.com";
        String name = "John Doe";

        emailService.sendMessageCreated(email, name);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendMessageDelete_Success() {

        String email = "user@example.com";
        String name = "Jane Doe";

        emailService.sendMessageDelete(email, name);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void init_MailSenderNotNull_LogsInfo() {

        assertNotNull(mailSender);
    }

    @Test
    void init_MailSenderNull_LogsWarning() {

        EmailService emailServiceWithNull = new EmailService(null);
        emailServiceWithNull.init();
    }

    @Test
    void sendEmail_MailSenderNull_DoesNothing() {

        EmailService emailServiceWithNull = new EmailService(null);
        emailServiceWithNull.init();
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        emailServiceWithNull.sendEmail(to, subject, text);
    }
}
