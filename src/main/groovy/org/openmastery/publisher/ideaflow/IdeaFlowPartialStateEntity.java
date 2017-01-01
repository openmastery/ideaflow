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
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.time.LocalDateTime;

@IdClass(IdeaFlowPartialStateEntity.PrimaryKey.class)
@Entity(name = "idea_flow_partial_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowPartialStateEntity {

	@Id
	private Long taskId;

	@Id
	@Enumerated(EnumType.STRING)
	private IdeaFlowPartialStateScope scope;

	private Long ownerId;

	@Enumerated(EnumType.STRING)
	private IdeaFlowStateType type;

	@Column(name = "start_time")
	private LocalDateTime start;

	private String startingComment;

	private boolean isLinkedToPrevious;
	private boolean isNested;

	public boolean isOfType(IdeaFlowStateType... typesToCheck) {
		for (IdeaFlowStateType typeToCheck : typesToCheck) {
			if (typeToCheck == type) {
				return true;
			}
		}
		return false;
	}


	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PrimaryKey implements Serializable {
		private long taskId;
		private IdeaFlowPartialStateScope scope;
	}

}
