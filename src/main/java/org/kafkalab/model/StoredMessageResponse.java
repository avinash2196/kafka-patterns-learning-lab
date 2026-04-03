package org.kafkalab.model;

import java.time.Instant;

/**
 * Read model returned by the API when listing stored messages.
 *
 * @param id generated identifier from the H2 database
 * @param data message value saved during publish flow
 * @param createdAt timestamp captured at persistence time
 */
public record StoredMessageResponse(Long id, String data, Instant createdAt) {
}