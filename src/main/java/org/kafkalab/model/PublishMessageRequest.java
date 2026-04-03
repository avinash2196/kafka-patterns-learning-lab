package org.kafkalab.model;

import javax.validation.constraints.NotBlank;

/**
 * Request payload used by the HTTP API when publishing a message.
 *
 * <p>Keeping request objects separate from persistence entities avoids coupling the external API to
 * the database model. That separation is a useful habit even in small learning projects.</p>
 *
 * @param data business value to publish to Kafka and store locally
 */
public record PublishMessageRequest(@NotBlank(message = "data is required") String data) {
}