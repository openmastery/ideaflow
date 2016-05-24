package org.ideaflow.publisher.core.ideaflow;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IdeaFlowStateRepository extends PagingAndSortingRepository<IdeaFlowStateEntity, Long> {

	List<IdeaFlowStateEntity> findByTaskId(long taskId);

}
