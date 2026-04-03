package org.kafkalab.data;

import java.util.List;
import org.kafkalab.model.KafkaMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for storing and reading {@link KafkaMessageEntity} demo records.
 *
 * <p>Spring Data generates the implementation automatically. That allows the learning project to
 * focus on message flow instead of boilerplate CRUD code.</p>
 */
@Repository
public interface KafkaMessageRepository extends JpaRepository<KafkaMessageEntity, Long> {

	/**
	 * Reads stored messages in ascending insertion order.
	 *
	 * <p>Learning Notes:</p>
	 * <ul>
	 *   <li>Spring Data derives the query from the method name.</li>
	 *   <li>The explicit ordering keeps API output deterministic for demos and tests.</li>
	 * </ul>
	 *
	 * @return all stored demo messages ordered by identifier
	 */
	List<KafkaMessageEntity> findAllByOrderByIdAsc();
}
