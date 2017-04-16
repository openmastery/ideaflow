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
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;
import org.openmastery.publisher.core.timeline.IdleTimeBandModel;
import org.openmastery.publisher.core.timeline.TimeBandModel;
import org.openmastery.time.TimeConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IdeaFlowBandModel extends TimeBandModel<IdeaFlowBandModel> {

	private Long id;
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String startingComment;
	private String endingComent;

	private IdeaFlowStateType type;

	private List<IdleTimeBandModel> idleBands = new ArrayList<>();
	private List<IdeaFlowBandModel> nestedBands = new ArrayList<>();

	public void addNestedBand(IdeaFlowBandModel ideaFlowBand) {
		nestedBands.add(ideaFlowBand);
	}

	public void addIdleBand(IdleTimeBandModel idleTimeBand) {
		idleBands.add(idleTimeBand);
	}

	public Duration getIdleDuration() {
		return TimeBandModel.sumDuration(idleBands);
	}

	@Override
	public Duration getDuration() {
		return Duration.between(start, end).minus(getIdleDuration());
	}

	public List<Positionable> getAllContentsFlattenedAsPositionableList() {
		// use a set b/c we could have duplicate idle bands (e.g. if idle is w/in nested conflict)
		// TODO: there's no test that fails if this is a List... either make it a list or add a test that proves the above statement
		HashSet<Positionable> positionables = new HashSet<>();
		positionables.addAll(idleBands);
		for (IdeaFlowBandModel nestedBand : nestedBands) {
			positionables.add(nestedBand);
			positionables.addAll(nestedBand.getAllContentsFlattenedAsPositionableList());
		}
		return new ArrayList<>(positionables);
	}

	@Override
	public List<TimeBandModel> getContainedBands() {
		ArrayList<TimeBandModel> containedBands = new ArrayList<>(nestedBands);
		containedBands.addAll(idleBands);
		return containedBands;
	}

	@Override
	protected IdeaFlowBandModel internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<IdeaFlowBandModel> splitNestedBands = TimeBandModel.splitAndReturnLeftSide(nestedBands, position);
		List<IdleTimeBandModel> splitIdleBands = TimeBandModel.splitAndReturnLeftSide(idleBands, position);
		return IdeaFlowBandModel.from(this)
				.end(position)
				.idleBands(splitIdleBands)
				.nestedBands(splitNestedBands)
				.build();
	}

	@Override
	protected IdeaFlowBandModel internalSplitAndReturnRightSide(LocalDateTime position) {
		List<IdeaFlowBandModel> splitNestedBands = TimeBandModel.splitAndReturnRightSide(nestedBands, position);
		List<IdleTimeBandModel> splitIdleBands = TimeBandModel.splitAndReturnRightSide(idleBands, position);
		return IdeaFlowBandModel.from(this)
				.start(position)
				.idleBands(splitIdleBands)
				.nestedBands(splitNestedBands)
				.build();
	}

	public static IdeaFlowBandModel.IdeaFlowBandModelBuilder from(IdeaFlowBandModel band) {
		return builder().id(band.id)
				.type(band.getType())
				.start(band.getStart())
				.end(band.getEnd())
				.idleBands(new ArrayList<>(band.getIdleBands()))
				.nestedBands(new ArrayList<>(band.getNestedBands()));
	}

}


