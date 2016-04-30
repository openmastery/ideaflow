package org.ideaflow.publisher.core.event;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<EventEntity, Long> {
}
