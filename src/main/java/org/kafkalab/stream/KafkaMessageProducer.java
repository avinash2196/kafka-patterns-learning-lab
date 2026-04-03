package org.kafkalab.stream;

import org.kafkalab.config.KafkaLabProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends messages to the configured Kafka topic.
 *
 * <p>This class keeps the Kafka-specific publishing code in one place so the rest of the tutorial
 * can stay focused on application flow.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>{@link KafkaTemplate} is Spring Kafka's high-level abstraction for producing records.</li>
 *   <li>The topic name comes from configuration so learners can experiment without changing code.</li>
 * </ul>
 */
@Component
public class KafkaMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaLabProperties kafkaLabProperties;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaLabProperties kafkaLabProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaLabProperties = kafkaLabProperties;
    }

    /**
     * Sends a message to the configured topic.
     *
     * @param message payload to publish
     */
    public void sendToTopic(String message) {
        log.info("Outgoing Message - Producing -> {}", message);
        kafkaTemplate.send(kafkaLabProperties.getTopicName(), message);
    }

    /**
     * Returns the configured topic name for response messages and documentation.
     *
     * @return configured topic name
     */
    public String getTopicName() {
        return kafkaLabProperties.getTopicName();
    }
}
