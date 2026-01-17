package org.example.notificationservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private boolean mailEnabled = true;

    @PostConstruct
    public void init() {
        if (mailSender == null) {
            log.warn("JavaMailSender не настроен. Email отправляться не будут.");
            mailEnabled = false;
        } else {
            log.info("EmailService инициализирован. Отправка email включена.");
        }
    }


    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email отправлен на: {}", to);
        } catch (Exception e) {
            log.error("Ошибка отправки email на {}: {}", to, e.getMessage());
        }
    }


    public void sendMessageCreated(String email, String name) {
        String subject = "Добро пожаловать!";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Ваш аккаунт на сайте был успешно создан.\n\n" +
                        "С уважением,\nКоманда сайта",
                name
        );

        sendEmail(email, subject, text);
    }


    public void sendMessageDelete(String email, String name) {
        String subject = "Ваш аккаунт был удален";
        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Ваш аккаунт был удалён.\n\n" +
                        "С уважением,\nКоманда сайта",
                name
        );

        sendEmail(email, subject, text);
    }
}
