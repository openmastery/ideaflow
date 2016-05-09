package org.ideaflow.publisher.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TimeBandGroup extends TimeBand<TimeBandGroup> {

	private long id;
	private long taskId;

	private List<IdeaFlowBand> linkedTimeBands;

	public void addLinkedTimeBand(IdeaFlowBand linkedIdeaFlowBand) {
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
		return TimeBand.sumDuration(linkedTimeBands);
	}

	@Override
	@JsonIgnore
	public List<? extends TimeBand> getContainedBands() {
		return getLinkedTimeBands();
	}

	@Override
	protected TimeBandGroup internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<IdeaFlowBand> splitLinkedBands = TimeBand.splitAndReturnLeftSide(linkedTimeBands, position);
		return from(this)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

	@Override
	protected TimeBandGroup internalSplitAndReturnRightSide(LocalDateTime position) {
		List<IdeaFlowBand> splitLinkedBands = TimeBand.splitAndReturnRightSide(linkedTimeBands, position);
		return from(this)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

	public static TimeBandGroup.TimeBandGroupBuilder from(TimeBandGroup group) {
		return builder().id(group.id)
				.taskId(group.taskId)
				.linkedTimeBands(new ArrayList<>(group.getLinkedTimeBands()));
	}

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.