package org.kafkalab.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides shared infrastructure primitives used by core application services.
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>Injecting time via {@link Clock} improves determinism in tests.</li>
 *   <li>Small configuration beans keep cross-cutting concerns explicit and reusable.</li>
 * </ul>
 *
 * <p>Design Notes:</p>
 * <ul>
 *   <li>Using UTC avoids timezone ambiguity in persisted timestamps.</li>
 *   <li>A dedicated bean prevents direct calls to {@code Instant.now()} from spreading across business code.</li>
 * </ul>
 */
@Configuration
public class LabCoreConfig {

    /**
     * Exposes the application clock.
     *
     * @return UTC system clock used by services for timestamp generation
     */
    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}