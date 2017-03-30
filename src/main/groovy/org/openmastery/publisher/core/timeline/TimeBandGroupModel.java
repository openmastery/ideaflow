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
package org.openmastery.publisher.core.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TimeBandGroupModel extends TimeBandModel<TimeBandGroupModel> {

	private String id;
	private long taskId;

	private List<IdeaFlowBandModel> linkedTimeBands;

	public void addLinkedTimeBand(IdeaFlowBandModel linkedIdeaFlowBand) {
		linkedTimeBands.add(linkedIdeaFlowBand);
	}

	public LocalDateTime getStart() {
		return linkedTimeBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		return linkedTimeBands.get(linkedTimeBands.size() - 1).getEnd();
	}

	@Override
	public Duration getDuration() {
		return sumDuration(linkedTimeBands);
	}

	@Override
	@JsonIgnore
	public List<? extends TimeBandModel> getContainedBands() {
		return getLinkedTimeBands();
	}

	@Override
	protected TimeBandGroupModel internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<IdeaFlowBandModel> splitLinkedBands = splitAndReturnLeftSide(linkedTimeBands, position);
		return from(this)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

	@Override
	protected TimeBandGroupModel internalSplitAndReturnRightSide(LocalDateTime position) {
		List<IdeaFlowBandModel> splitLinkedBands = splitAndReturnRightSide(linkedTimeBands, position);
		return from(this)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

	public static TimeBandGroupModel.TimeBandGroupModelBuilder from(TimeBandGroupModel group) {
		return builder().id(group.id)
				.taskId(group.taskId)
				.linkedTimeBands(new ArrayList<>(group.getLinkedTimeBands()));
	}

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group description is first description in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.