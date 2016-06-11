package org.openmastery.publisher.core.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.publisher.api.timeline.TimeBand;
import org.openmastery.publisher.api.timeline.TimeBandComparator;
import org.openmastery.publisher.api.timeline.TimeBandGroup;

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
	private List<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
	private List<TimeBandGroup> timeBandGroups = new ArrayList<>();
	private List<Event> events = new ArrayList<>();

	public List<TimeBand> getAllTimeBands() {
		List<TimeBand> allTimeBands = new ArrayList<>(ideaFlowBands);
		allTimeBands.addAll(timeBandGroups);
		return allTimeBands;
	}

	public List<TimeBand> getAllTimeBandsSortedByStartTime() {
		List<TimeBand> allTimeBands = getAllTimeBands();
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		return allTimeBands;
	}

	public LocalDateTime getStart() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(sortedTimeBands.size() - 1).getStart();
	}

	public long getRelativeStart() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getRelativeStart();
	}

	public Duration getDuration() {
		Duration duration = TimeBand.sumDuration(ideaFlowBands);
		return duration.plus(TimeBand.sumDuration(timeBandGroups));
	}

	public void addTimeBand(TimeBand timeBand) {
		if (timeBand instanceof IdeaFlowBand) {
			ideaFlowBands.add((IdeaFlowBand) timeBand);
		} else if (timeBand instanceof TimeBandGroup) {
			timeBandGroups.add((TimeBandGroup) timeBand);
		} else {
			throw new RuntimeException("Unexpected time band=" + timeBand);
		}
	}

	public void addEvent(Event event) {
		events.add(event);
	}

}
