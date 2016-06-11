package org.openmastery.publisher.core.event;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<EventEntity, Long> {

	List<EventEntity> findByTaskId(long taskId);

}
