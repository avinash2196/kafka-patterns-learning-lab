package org.kafkalab.model;

import java.time.Instant;

/**
 * Response returned after a message is published.
 *
 * <p>This summary gives learners immediate feedback about the topic used, the generated database
 * identifier, and the time at which the message was stored.</p>
 *
 * @param id generated identifier from the H2 database
 * @param data message value that was published
 * @param topicName Kafka topic used for the publish operation
 * @param storedAt timestamp captured when the message was persisted locally
 * @param status human-readable status text for the tutorial workflow
 */
public record PublishedMessageResponse(
        Long id,
        String data,
        String topicName,
        Instant storedAt,
        String status
) {
}