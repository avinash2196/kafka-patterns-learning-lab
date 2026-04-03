package org.kafkalab.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kafkalab.model.PublishMessageRequest;
import org.kafkalab.model.PublishedMessageResponse;
import org.kafkalab.model.StoredMessageResponse;
import org.kafkalab.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Test
        void givenValidPayload_whenPublishMessage_thenReturnsAcceptedResponse() throws Exception {
        PublishedMessageResponse response = new PublishedMessageResponse(
                1L,
                "hello kafka",
                "companies",
                Instant.parse("2026-04-02T10:15:30Z"),
                "Message published to Kafka and stored in H2"
        );

        when(messageService.publishMessage(any(PublishMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"hello kafka\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.data").value("hello kafka"))
                .andExpect(jsonPath("$.topicName").value("companies"));

        verify(messageService).publishMessage(any(PublishMessageRequest.class));
    }

    @Test
        void givenBlankPayload_whenPublishMessage_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
        void givenStoredMessages_whenListMessages_thenReturnsOkWithPayload() throws Exception {
                when(messageService.listStoredMessages()).thenReturn(List.of(
                new StoredMessageResponse(1L, "hello kafka", Instant.parse("2026-04-02T10:15:30Z"))
        ));

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].data").value("hello kafka"));
    }
}