package org.kafkalab.controller;

import java.util.List;
import javax.validation.Valid;
import org.kafkalab.model.PublishMessageRequest;
import org.kafkalab.model.PublishedMessageResponse;
import org.kafkalab.model.StoredMessageResponse;
import org.kafkalab.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the learning-lab HTTP API.
 *
 * <p>The controller presents a small surface area on purpose: one endpoint publishes a message and
 * another endpoint shows what has been stored locally. This keeps the example approachable for
 * developers who are new to Spring MVC and Kafka integrations.</p>
 *
 * <p>Learning Notes:</p>
 * <ul>
 *   <li>Controllers translate HTTP requests into service-layer operations.</li>
 *   <li>Returning response objects makes API behavior easier to inspect with curl or Postman.</li>
 * </ul>
 *
 * <p>Design Notes:</p>
 * <ul>
 *   <li>The API is intentionally synchronous and minimal to emphasize readability over features.</li>
 *   <li>Validation is applied at the edge so the rest of the code can assume valid input.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Publishes a new demo message.
     *
     * <p>The request payload is validated, forwarded to Kafka, and then mirrored into H2 so that
     * learners can inspect the stored result without connecting additional tooling.</p>
     *
     * @param request request body containing the message text
     * @return a small summary of what the application just published and stored
     */
    @PostMapping
    public ResponseEntity<PublishedMessageResponse> publishMessage(@Valid @RequestBody PublishMessageRequest request) {
        PublishedMessageResponse response = messageService.publishMessage(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Lists messages stored in the local H2 database.
     *
     * @return stored messages ordered by insertion sequence
     */
    @GetMapping
    public List<StoredMessageResponse> listMessages() {
        return messageService.listStoredMessages();
    }
}
