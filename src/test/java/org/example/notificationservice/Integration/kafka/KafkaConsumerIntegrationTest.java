package org.example.notificationservice.Integration.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.notificationservice.NotificationServiceApplication;
import org.example.notificationservice.dto.UserEventMessage;
import org.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = NotificationServiceApplication.class,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        }
)
@EmbeddedKafka(
        topics = "user-events",
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "port=0"
        }
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @Test
    void consumeUserCreatedEvent_Success() throws Exception {

        UserEventMessage event = new UserEventMessage();
        event.setEventType("USER_CREATED");
        event.setUserEmail("newuser@example.com");
        event.setUserName("New User");

        String topic = "user-events";
        String message = objectMapper.writeValueAsString(event);


        Map<String, Object> producerProps = new HashMap<>(
                KafkaTestUtils.producerProps(embeddedKafkaBroker)
        );
        Producer<String, String> producer = new DefaultKafkaProducerFactory<String, String>(producerProps)
                .createProducer();

        producer.send(new ProducerRecord<>(topic, message));
        producer.flush();

        Thread.sleep(3000);

        verify(emailService, timeout(5000).times(1))
                .sendMessageCreated("newuser@example.com", "New User");
    }

    @Test
    void consumeUserDeletedEvent_Success() throws Exception {

        UserEventMessage event = new UserEventMessage();
        event.setEventType("USER_DELETED");
        event.setUserEmail("olduser@example.com");
        event.setUserName("Old User");

        String topic = "user-events";
        String message = objectMapper.writeValueAsString(event);

        Map<String, Object> producerProps = new HashMap<>(
                KafkaTestUtils.producerProps(embeddedKafkaBroker)
        );
        Producer<String, String> producer = new DefaultKafkaProducerFactory<String, String>(producerProps)
                .createProducer();

        producer.send(new ProducerRecord<>(topic, message));
        producer.flush();

        Thread.sleep(3000);

        verify(emailService, timeout(5000).times(1))
                .sendMessageDelete("olduser@example.com", "Old User");
    }
}
