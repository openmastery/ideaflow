package org.openmastery.publisher.core.ideaflow;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IdeaFlowPartialStateRepository extends PagingAndSortingRepository<IdeaFlowPartialStateEntity, IdeaFlowPartialStateEntity.PrimaryKey> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from idea_flow_partial_state where task_id = ?1 and scope = ?2")
	int deleteIfExists(Long taskId, String scope);

}
