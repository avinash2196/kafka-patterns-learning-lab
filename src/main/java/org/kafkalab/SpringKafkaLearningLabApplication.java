package org.kafkalab;

import org.kafkalab.config.KafkaLabProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry point for the Spring Kafka Learning Lab sample application.
 *
 * <p>This application intentionally keeps the architecture small so developers can focus on the
 * message flow between an HTTP endpoint, a Kafka producer, a Kafka consumer, and a simple H2-backed
 * persistence layer.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>{@code @SpringBootApplication} enables component scanning and common auto-configuration.</li>
 *   <li>{@code @EnableConfigurationProperties} binds tutorial settings from {@code application.yml}
 *   into a strongly typed Java class.</li>
 * </ul>
 */
@SpringBootApplication
@EnableConfigurationProperties(KafkaLabProperties.class)
public class SpringKafkaLearningLabApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaLearningLabApplication.class, args);
    }
}