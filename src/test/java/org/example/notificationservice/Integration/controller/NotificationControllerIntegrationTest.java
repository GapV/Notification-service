package org.example.notificationservice.Integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.notificationservice.NotificationServiceApplication;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = NotificationServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @Test
    void sendEmail_Success() throws Exception {

        Map<String, String> request = new HashMap<>();
        request.put("to", "test@example.com");
        request.put("subject", "Test Subject");
        request.put("text", "Test Body");


        mockMvc.perform(post("/api/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email отправлен на test@example.com"));

        verify(emailService, times(1))
                .sendEmail("test@example.com", "Test Subject", "Test Body");
    }

    @Test
    void sendEmail_MissingRequiredFields() throws Exception {

        Map<String, String> request = new HashMap<>();
        request.put("to", "test@example.com");
        request.put("subject", "Test Subject");

        mockMvc.perform(post("/api/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Необходимы поля: to, subject, text"));

        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void sendEmail_EmptyFields() throws Exception {

        Map<String, String> request = new HashMap<>();
        request.put("to", "");
        request.put("subject", "");
        request.put("text", "");


        mockMvc.perform(post("/api/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email отправлен на "));

        verify(emailService, times(1)).sendEmail("", "", "");
    }

    @Test
    void healthCheck_Success() throws Exception {

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Service работает"));
    }

}