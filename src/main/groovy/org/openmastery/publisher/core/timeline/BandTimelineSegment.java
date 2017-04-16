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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.PositionableComparator;
import org.openmastery.publisher.core.activity.ActivityModel;
import org.openmastery.publisher.core.event.EventModel;
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandTimelineSegment {

	private Long id;
	private String description;
	private List<IdeaFlowBandModel> ideaFlowBands = new ArrayList<>();
	private List<TimeBandGroupModel> timeBandGroups = new ArrayList<>();
	private List<EventModel> events = new ArrayList<>();
	private List<ActivityModel> activities = new ArrayList<>();

	public List<TimeBandModel> getAllTimeBands() {
		List<TimeBandModel> allTimeBands = new ArrayList<>(ideaFlowBands);
		allTimeBands.addAll(timeBandGroups);
		return allTimeBands;
	}

	public List<TimeBandModel> getAllTimeBandsSortedByStartTime() {
		List<TimeBandModel> allTimeBands = getAllTimeBands();
		Collections.sort(allTimeBands, PositionableComparator.INSTANCE);
		return allTimeBands;
	}

	public List<Positionable> getAllContentsFlattenedAsPositionableList() {
		// use a set b/c we could have duplicate idle bands (e.g. if idle is w/in nested conflict)
		// TODO: there's no test that fails if this is a List... either make it a list or add a test that proves the above statement
		HashSet<Positionable> positionables = new HashSet<>();
		addTimeBandsAndContainedBandsToTargetList(positionables, getAllTimeBands());
		positionables.addAll(events);
		positionables.addAll(activities);
		return new ArrayList<>(positionables);
	}

	private void addTimeBandsAndContainedBandsToTargetList(Set<Positionable> targetSet, List<TimeBandModel> bandsToAdd) {
		for (TimeBandModel bandToAdd : bandsToAdd) {
			targetSet.add(bandToAdd);
			addTimeBandsAndContainedBandsToTargetList(targetSet, bandToAdd.getContainedBands());
		}
	}

	public LocalDateTime getStart() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(sortedTimeBands.size() - 1).getStart();
	}

	public long getRelativePositionInSeconds() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getRelativePositionInSeconds();
	}

	public Duration getDuration() {
		Duration duration = TimeBandModel.sumDuration(ideaFlowBands);
		return duration.plus(TimeBandModel.sumDuration(timeBandGroups));
	}

	public void addTimeBand(TimeBandModel timeBand) {
		if (timeBand instanceof IdeaFlowBandModel) {
			ideaFlowBands.add((IdeaFlowBandModel) timeBand);
		} else if (timeBand instanceof TimeBandGroupModel) {
			timeBandGroups.add((TimeBandGroupModel) timeBand);
		} else {
			throw new RuntimeException("Unexpected time band=" + timeBand);
		}
	}

	public void addEvent(EventModel event) {
		events.add(event);
	}

	public void addActivity(ActivityModel activity) {
		activities.add(activity);
	}

}
