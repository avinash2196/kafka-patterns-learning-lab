package org.kafkalab.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.kafkalab.data.KafkaMessageRepository;
import org.kafkalab.model.KafkaMessageEntity;
import org.kafkalab.model.PublishMessageRequest;
import org.kafkalab.model.PublishedMessageResponse;
import org.kafkalab.model.StoredMessageResponse;
import org.kafkalab.stream.KafkaMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Coordinates the learning-lab message workflow.
 *
 * <p>This service is the main orchestration point for the sample. It publishes a value to Kafka,
 * stores the same value in H2, and exposes a read-friendly representation for the REST API.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>Keeping orchestration in a service avoids placing business steps directly in the controller.</li>
 *   <li>Persisting the message locally makes the Kafka flow observable without extra tooling.</li>
 * </ul>
 *
 * <p>Design Notes:</p>
 * <ul>
 *   <li>The service publishes first, then stores locally, to keep the order of operations easy to follow.</li>
 *   <li>Error handling is intentionally lightweight because this repository is a teaching sample.</li>
 * </ul>
 */
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private static final String PUBLISH_SUCCESS_STATUS = "Message published to Kafka and stored in H2";

    private final KafkaMessageProducer kafkaMessageProducer;
    private final KafkaMessageRepository kafkaMessageRepository;
    private final Clock clock;

    public MessageService(
            KafkaMessageProducer kafkaMessageProducer,
            KafkaMessageRepository kafkaMessageRepository,
            Clock clock
    ) {
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.kafkaMessageRepository = kafkaMessageRepository;
        this.clock = clock;
    }

    /**
     * Publishes a message to Kafka and saves a copy in H2 for learning visibility.
     *
     * @param request payload sent from the API layer
     * @return summary of the stored message for API clients
     */
    public PublishedMessageResponse publishMessage(PublishMessageRequest request) {
        log.info("Publishing tutorial payload: {}", request.data());
        String message = request.data();
        kafkaMessageProducer.sendToTopic(message);

        KafkaMessageEntity kafkaMessageEntity = new KafkaMessageEntity();
        kafkaMessageEntity.setData(message);
        kafkaMessageEntity.setCreatedAt(Instant.now(clock));

        KafkaMessageEntity savedEntity = kafkaMessageRepository.save(kafkaMessageEntity);

        return new PublishedMessageResponse(
                savedEntity.getId(),
                savedEntity.getData(),
                kafkaMessageProducer.getTopicName(),
                savedEntity.getCreatedAt(),
                PUBLISH_SUCCESS_STATUS
        );
    }

    /**
     * Retrieves stored messages for the read-only tutorial endpoint.
     *
     * @return stored messages mapped into API response objects
     */
    public List<StoredMessageResponse> listStoredMessages() {
        return kafkaMessageRepository.findAllByOrderByIdAsc().stream()
                .map(this::toStoredMessageResponse)
                .collect(Collectors.toList());
    }

    private StoredMessageResponse toStoredMessageResponse(KafkaMessageEntity entity) {
        return new StoredMessageResponse(entity.getId(), entity.getData(), entity.getCreatedAt());
    }
}
