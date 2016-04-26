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
	public IdeaFlowBand splitAndReturnLeftSide(LocalDateTime position) {
		if (startsOnOrAfter(position)) {
			return null;
		} else if (endsOnOrBefore(position)) {
			return this;
		} else {
			List<IdeaFlowBand> splitNestedBands = new ArrayList<>();
			for (IdeaFlowBand nestedBand : nestedBands) {
				IdeaFlowBand splitNestedBand = nestedBand.splitAndReturnLeftSide(position);
				if (splitNestedBand != null) {
					splitNestedBands.add(splitNestedBand);
				}
			}

			IdeaFlowBand leftBand = IdeaFlowBand.from(this)
					.end(position)
					.nestedBands(splitNestedBands)
					.build();
			return leftBand;
		}
	}

	@Override
	public IdeaFlowBand splitAndReturnRightSide(LocalDateTime position) {
		if (endsOnOrBefore(position)) {
			return null;
		} else if (startsOnOrAfter(position)) {
			return this;
		} else {
			List<IdeaFlowBand> splitNestedBands = new ArrayList<>();
			for (IdeaFlowBand nestedBand : nestedBands) {
				IdeaFlowBand splitNestedBand = nestedBand.splitAndReturnRightSide(position);
				if (splitNestedBand != null) {
					splitNestedBands.add(splitNestedBand);
				}
			}

			IdeaFlowBand rightBand = IdeaFlowBand.from(this)
					.start(position)
					.nestedBands(splitNestedBands)
					.build();
			return rightBand;
		}
	}

	public static Duration sumDuration(List<IdeaFlowBand> ideaFlowBands) {
		Duration duration = Duration.ZERO;
		for (IdeaFlowBand ideaFlowBand : ideaFlowBands) {
			duration = duration.plus(ideaFlowBand.getDuration());
		}
		return duration;
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


