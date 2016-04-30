package org.ideaflow.publisher.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: rename > IdleTimeBandEntity
public class IdleTimeBand extends TimeBand<IdleTimeBand> {

	private long id;
	private long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String comment;

	private boolean auto;

	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}

	@Override
	@JsonIgnore
	public List<TimeBand> getContainedBands() {
		return Collections.EMPTY_LIST;
	}

	@Override
	protected IdleTimeBand internalSplitAndReturnLeftSide(LocalDateTime position) {
		return from(this)
				.end(position)
				.build();
	}

	@Override
	protected IdleTimeBand internalSplitAndReturnRightSide(LocalDateTime position) {
		return from(this)
				.start(position)
				.build();
	}

	public static IdleTimeBand.IdleTimeBandBuilder from(IdleTimeBand idle) {
		return builder().id(idle.id)
				.taskId(idle.taskId)
				.start(idle.start)
				.end(idle.end)
				.comment(idle.comment)
				.auto(idle.auto);
	}

}
