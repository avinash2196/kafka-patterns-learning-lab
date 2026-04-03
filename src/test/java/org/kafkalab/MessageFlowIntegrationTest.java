package org.kafkalab;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Exercises the main tutorial flow against the Spring context with an embedded Kafka broker.
 *
 * <p>This test demonstrates a practical middle ground between isolated unit tests and full manual
 * testing. It verifies that the HTTP API, service, repository, and Kafka wiring work together.</p>
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = "companies")
class MessageFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publishMessageShouldStoreMessageAndExposeItThroughReadEndpoint() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"integration message\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data").value("integration message"))
                .andExpect(jsonPath("$.topicName").value("companies"));

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].data").value("integration message"));
    }
}