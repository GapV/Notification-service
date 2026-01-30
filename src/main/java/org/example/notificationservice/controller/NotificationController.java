package org.example.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "API для отправки email уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    @Operation(
            summary = "Отправить email",
            description = "Отправляет email на указанный адрес"
    )
    public ResponseEntity<String> sendEmail(
            @Parameter(description = "Данные для отправки email. Обязательные поля: to, subject, text")
            @RequestBody Map<String, String> request) {

        String to = request.get("to");
        String subject = request.get("subject");
        String text = request.get("text");

        if (to == null || subject == null || text == null) {
            return ResponseEntity.badRequest().body("Необходимы поля: to, subject, text");
        }

        emailService.sendEmail(to, subject, text);
        return ResponseEntity.ok("Email отправлен на " + to);
    }

    @GetMapping("/health")
    @Operation(
            summary = "Проверка здоровья сервиса",
            description = "Проверяет работоспособность сервиса"
    )
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service работает");
    }
}
