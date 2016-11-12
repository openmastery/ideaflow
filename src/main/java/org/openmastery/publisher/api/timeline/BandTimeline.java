package org.openmastery.publisher.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.activity.NewModificationActivity;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandTimeline {

	private String description;
	private LocalDateTime start;
	private LocalDateTime end;
	private Long durationInSeconds;
	private Long relativePositionInSeconds;
	private List<IdeaFlowBand> ideaFlowBands;
	private List<TimeBandGroup> timeBandGroups;

	private List<ModificationActivity> modificationActivities;
	private List<ExecutionEvent> executionEvents;
	private List<CalendarEvent> calendarEvents;
	private List<Event> notes;


	// simplify dozer mapping

	@JsonIgnore
	public Long getDuration() {
		return durationInSeconds;
	}

	@JsonIgnore
	public void setDuration(Long duration) {
		durationInSeconds = duration;
	}

}
