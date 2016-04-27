package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

	private Duration idle;

	private List<IdeaFlowBand> nestedBands = new ArrayList<>();

	public void addNestedBand(IdeaFlowBand ideaFlowBand) {
		nestedBands.add(ideaFlowBand);
	}

	public Duration getDuration() {
		return Duration.between(start, end).minus(idle);
	}

	@Override
	protected IdeaFlowBand internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<IdeaFlowBand> splitNestedBands = TimeBand.splitAndReturnLeftSide(nestedBands, position);
		IdeaFlowBand leftBand = IdeaFlowBand.from(this)
				.end(position)
				.nestedBands(splitNestedBands)
				.build();
		return leftBand;
	}

	@Override
	protected IdeaFlowBand internalSplitAndReturnRightSide(LocalDateTime position) {
		List<IdeaFlowBand> splitNestedBands = TimeBand.splitAndReturnRightSide(nestedBands, position);
		IdeaFlowBand rightBand = IdeaFlowBand.from(this)
				.start(position)
				.nestedBands(splitNestedBands)
				.build();
		return rightBand;
	}

	public static IdeaFlowBand.IdeaFlowBandBuilder from(IdeaFlowBand band) {
		return builder().id(band.id)
				.type(band.getType())
				.idle(band.getIdle())
				.start(band.getStart())
				.end(band.getEnd())
				.nestedBands(band.getNestedBands());
	}

}


