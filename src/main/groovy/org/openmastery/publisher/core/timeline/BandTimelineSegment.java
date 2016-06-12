package org.openmastery.publisher.core.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandTimelineSegment {

	private Long id;
	private String description;
	private List<IdeaFlowBandModel> ideaFlowBands = new ArrayList<>();
	private List<TimeBandGroupModel> timeBandGroups = new ArrayList<>();
	private List<Event> events = new ArrayList<>();

	public List<TimeBandModel> getAllTimeBands() {
		List<TimeBandModel> allTimeBands = new ArrayList<>(ideaFlowBands);
		allTimeBands.addAll(timeBandGroups);
		return allTimeBands;
	}

	public List<TimeBandModel> getAllTimeBandsSortedByStartTime() {
		List<TimeBandModel> allTimeBands = getAllTimeBands();
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		return allTimeBands;
	}

	public LocalDateTime getStart() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(sortedTimeBands.size() - 1).getStart();
	}

	public long getRelativeStart() {
		List<TimeBandModel> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getRelativeStart();
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

	public void addEvent(Event event) {
		events.add(event);
	}

}
