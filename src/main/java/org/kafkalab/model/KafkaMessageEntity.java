package org.kafkalab.model;

import java.time.Instant;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * JPA entity used to persist Kafka payloads in the in-memory H2 database.
 *
 * <p>This entity is intentionally small. It shows how a learning project can mirror published
 * messages into a relational store without introducing advanced persistence patterns too early.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>Entities represent the database shape, not necessarily the external API shape.</li>
 *   <li>A timestamp column makes it easier to inspect message order during local experiments.</li>
 * </ul>
 */
@Entity
public class KafkaMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Message value mirrored from Kafka payload. */
    @Column(nullable = false)
    private String data;

    /** Timestamp used to show when the local mirror record was created. */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
