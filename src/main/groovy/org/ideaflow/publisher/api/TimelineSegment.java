package org.ideaflow.publisher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineSegment {

	private List<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
	private List<TimeBandGroup> timeBandGroups = new ArrayList<>();
	private List<Event> events = new ArrayList<>();

	@JsonIgnore
	public List<TimeBand> getAllTimeBands() {
		List<TimeBand> allTimeBands = new ArrayList<>(ideaFlowBands);
		allTimeBands.addAll(timeBandGroups);
		return allTimeBands;
	}

	@JsonIgnore
	public List<TimeBand> getAllTimeBandsSortedByStartTime() {
		List<TimeBand> allTimeBands = getAllTimeBands();
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		return allTimeBands;
	}

	@JsonGetter
	public LocalDateTime getStart() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getStart();
	}

	@JsonGetter
	public LocalDateTime getEnd() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(sortedTimeBands.size() - 1).getStart();
	}

	@JsonGetter
	public long getRelativeStart() {
		List<TimeBand> sortedTimeBands = getAllTimeBandsSortedByStartTime();
		return sortedTimeBands.get(0).getRelativeStart();
	}

	@JsonGetter
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
