package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ideaflow.publisher.core.activity.IdleTimeBand;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IdeaFlowBand extends TimeBand<IdeaFlowBand> {

	private long id;

	private LocalDateTime start;
	private LocalDateTime end;

	private IdeaFlowStateType type;

	private List<IdleTimeBand> idleBands = new ArrayList<>();
	private List<IdeaFlowBand> nestedBands = new ArrayList<>();

	public void addNestedBand(IdeaFlowBand ideaFlowBand) {
		nestedBands.add(ideaFlowBand);
	}

	public void addIdleBand(IdleTimeBand idleTimeBand) {
		idleBands.add(idleTimeBand);
	}

	public Duration getIdleDuration() {
		return TimeBand.sumDuration(idleBands);
	}

	public Duration getDuration() {
		return Duration.between(start, end).minus(getIdleDuration());
	}

	@Override
	protected IdeaFlowBand internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<IdeaFlowBand> splitNestedBands = TimeBand.splitAndReturnLeftSide(nestedBands, position);
		List<IdleTimeBand> splitIdleBands = TimeBand.splitAndReturnLeftSide(idleBands, position);
		IdeaFlowBand leftBand = IdeaFlowBand.from(this)
				.end(position)
				.idleBands(splitIdleBands)
				.nestedBands(splitNestedBands)
				.build();
		return leftBand;
	}

	@Override
	protected IdeaFlowBand internalSplitAndReturnRightSide(LocalDateTime position) {
		List<IdeaFlowBand> splitNestedBands = TimeBand.splitAndReturnRightSide(nestedBands, position);
		List<IdleTimeBand> splitIdleBands = TimeBand.splitAndReturnRightSide(idleBands, position);
		IdeaFlowBand rightBand = IdeaFlowBand.from(this)
				.start(position)
				.idleBands(splitIdleBands)
				.nestedBands(splitNestedBands)
				.build();
		return rightBand;
	}

	public static IdeaFlowBand.IdeaFlowBandBuilder from(IdeaFlowBand band) {
		return builder().id(band.id)
				.type(band.getType())
				.start(band.getStart())
				.end(band.getEnd())
				.idleBands(new ArrayList<>(band.getIdleBands()))
				.nestedBands(new ArrayList<>(band.getNestedBands()));
	}

}


