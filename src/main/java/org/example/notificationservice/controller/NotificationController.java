package org.example.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
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
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service работает");
    }
}
