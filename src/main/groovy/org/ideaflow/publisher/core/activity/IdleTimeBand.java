package org.ideaflow.publisher.core.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ideaflow.publisher.api.TimeBand;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleTimeBand extends TimeBand<IdleTimeBand> {

	private long id;
	private long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String comment;

	private boolean auto;

	public Duration getDuration() {
		return Duration.between(start, end);
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
