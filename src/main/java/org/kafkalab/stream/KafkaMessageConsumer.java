package org.kafkalab.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Consumes messages from the demo topic as part of a consumer group.
 *
 * <p>The consumer simply logs payload metadata. That choice keeps the example focused on how a
 * listener receives records rather than on downstream processing concerns.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>{@code @KafkaListener} creates a listener container behind the scenes.</li>
 *   <li>Headers such as partition and offset help explain how Kafka tracks records.</li>
 * </ul>
 */
@Component
public class KafkaMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    /**
     * Handles incoming Kafka records and logs payload with partition and offset metadata.
     *
     * @param data consumed message value
     * @param partition partition from which the message was read
     * @param offset offset position of the consumed record
     */
    @KafkaListener(topics = "${learning-lab.kafka.topic-name}", groupId = "${learning-lab.kafka.consumer-group-id}")
    public void consume(
            String data,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Incoming Message -> data: {}, partition: {}, offset: {}", data, partition, offset);
    }
}
