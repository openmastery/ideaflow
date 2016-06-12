package org.openmastery.publisher.core.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel;

import java.time.Duration;
import java.time.LocalDateTime;
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

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.