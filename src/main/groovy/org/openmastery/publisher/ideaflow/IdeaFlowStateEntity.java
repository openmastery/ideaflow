/**
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.ideaflow;

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
	private Long id;
	private Long taskId;
	private Long ownerId;

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
