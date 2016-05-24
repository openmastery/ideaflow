package org.ideaflow.publisher.core.activity;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IdleTimeBandRepository extends PagingAndSortingRepository<IdleTimeBandEntity, Long> {

	List<IdleTimeBandEntity> findByTaskId(long taskId);

}
