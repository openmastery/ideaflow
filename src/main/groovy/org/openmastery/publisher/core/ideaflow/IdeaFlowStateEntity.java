package org.openmastery.publisher.core.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Entity(name = "idea_flow_state")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowStateEntity implements Comparable<IdeaFlowStateEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idea_flow_state_seq_gen")
	@SequenceGenerator(name = "idea_flow_state_seq_gen", sequenceName = "idea_flow_state_seq")
	private long id;

	private long taskId;

	@Enumerated(EnumType.STRING)
	private IdeaFlowStateType type;

	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;

	private String startingComment;
	private String endingComment;

	private boolean isLinkedToPrevious;
	private boolean isNested;

	public static IdeaFlowStateEntity.IdeaFlowStateEntityBuilder from(IdeaFlowPartialStateEntity state) {
		return builder().type(state.getType())
				.start(state.getStart())
				.startingComment(state.getStartingComment())
				.isLinkedToPrevious(state.isLinkedToPrevious())
				.isNested(state.isNested())
				.taskId(state.getTaskId());
	}

	public static IdeaFlowStateEntity.IdeaFlowStateEntityBuilder from(IdeaFlowStateEntity state) {
		return builder().type(state.getType())
				.start(state.getStart())
				.end(state.getEnd())
				.startingComment(state.getStartingComment())
				.endingComment(state.getEndingComment())
				.isLinkedToPrevious(state.isLinkedToPrevious())
				.isNested(state.isNested())
				.taskId(state.getTaskId());
	}

	@Override
	public int compareTo(IdeaFlowStateEntity o) {
		return start.compareTo(o.start);
	}

}
