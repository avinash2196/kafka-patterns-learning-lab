package org.kafkalab.config;

import javax.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Centralizes tutorial-specific Kafka settings.
 *
 * <p>This class separates application-specific concepts, such as the demo topic name and consumer
 * group, from framework configuration. That makes the sample easier to understand because learners
 * can see which values belong to the exercise itself.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>{@code @ConfigurationProperties} maps hierarchical YAML properties into a typed object.</li>
 *   <li>Validation prevents the application from starting with missing tutorial settings.</li>
 * </ul>
 */
@Validated
@ConfigurationProperties(prefix = "learning-lab.kafka")
public class KafkaLabProperties {

    @NotBlank
    private String topicName;

    @NotBlank
    private String consumerGroupId;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }
}