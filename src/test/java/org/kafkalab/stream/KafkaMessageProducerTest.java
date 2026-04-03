package org.kafkalab.stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kafkalab.config.KafkaLabProperties;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Unit tests for {@link KafkaMessageProducer}.
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>Mocks isolate producer logic from a live Kafka broker.</li>
 *   <li>The test verifies that topic selection comes from typed properties.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class KafkaMessageProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private KafkaLabProperties kafkaLabProperties;

    private KafkaMessageProducer kafkaMessageProducer;

    @BeforeEach
    void setUp() {
        kafkaMessageProducer = new KafkaMessageProducer(kafkaTemplate, kafkaLabProperties);
        when(kafkaLabProperties.getTopicName()).thenReturn("companies");
    }

    @Test
    void sendToTopicShouldUseConfiguredTopicName() {
        kafkaMessageProducer.sendToTopic("hello producer");

        verify(kafkaTemplate).send("companies", "hello producer");
    }

    @Test
    void getTopicNameShouldExposeConfiguredValue() {
        String topicName = kafkaMessageProducer.getTopicName();

        org.junit.jupiter.api.Assertions.assertEquals("companies", topicName);
    }
}
