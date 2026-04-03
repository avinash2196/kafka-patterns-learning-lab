package org.kafkalab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kafkalab.data.KafkaMessageRepository;
import org.kafkalab.model.KafkaMessageEntity;
import org.kafkalab.model.PublishMessageRequest;
import org.kafkalab.model.PublishedMessageResponse;
import org.kafkalab.model.StoredMessageResponse;
import org.kafkalab.stream.KafkaMessageProducer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private KafkaMessageProducer kafkaMessageProducer;

    @Mock
    private KafkaMessageRepository kafkaMessageRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(kafkaMessageProducer, kafkaMessageRepository);
    }

    @Test
    void givenValidRequest_whenPublishMessage_thenSendsToKafkaAndStoresEntity() {
        KafkaMessageEntity savedEntity = new KafkaMessageEntity();
        savedEntity.setId(1L);
        savedEntity.setData("hello kafka");
        savedEntity.setCreatedAt(Instant.parse("2026-04-02T10:15:30Z"));

        when(kafkaMessageProducer.getTopicName()).thenReturn("companies");
        when(kafkaMessageRepository.save(any(KafkaMessageEntity.class))).thenReturn(savedEntity);

        PublishedMessageResponse response = messageService.publishMessage(new PublishMessageRequest("hello kafka"));

        verify(kafkaMessageProducer).sendToTopic("hello kafka");

        ArgumentCaptor<KafkaMessageEntity> entityCaptor = ArgumentCaptor.forClass(KafkaMessageEntity.class);
        verify(kafkaMessageRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getData()).isEqualTo("hello kafka");
        assertThat(entityCaptor.getValue().getCreatedAt()).isNotNull();

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.data()).isEqualTo("hello kafka");
        assertThat(response.topicName()).isEqualTo("companies");
        assertThat(response.status()).contains("published");
    }

    @Test
    void givenStoredEntities_whenListStoredMessages_thenMapsEntitiesToResponses() {
        KafkaMessageEntity first = new KafkaMessageEntity();
        first.setId(1L);
        first.setData("first");
        first.setCreatedAt(Instant.parse("2026-04-02T10:00:00Z"));

        KafkaMessageEntity second = new KafkaMessageEntity();
        second.setId(2L);
        second.setData("second");
        second.setCreatedAt(Instant.parse("2026-04-02T10:05:00Z"));

        when(kafkaMessageRepository.findAllByOrderByIdAsc()).thenReturn(List.of(first, second));

        List<StoredMessageResponse> messages = messageService.listStoredMessages();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).data()).isEqualTo("first");
        assertThat(messages.get(1).data()).isEqualTo("second");
    }
}