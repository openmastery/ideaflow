package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.timeline.IdleTimeBand;
import org.openmastery.publisher.api.timeline.TimeBand;

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
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String startingComment;
	private String endingComent;

	private IdeaFlowStateType type;

	@JsonIgnore
	private List<IdleTimeBand> idleBands = new ArrayList<>();
	private List<IdeaFlowBand> nestedBands = new ArrayList<>();

	public void addNestedBand(IdeaFlowBand ideaFlowBand) {
		nestedBands.add(ideaFlowBand);
	}

	public void addIdleBand(IdleTimeBand idleTimeBand) {
		idleBands.add(idleTimeBand);
	}

	@JsonIgnore
	public Duration getIdleDuration() {
		return TimeBand.sumDuration(idleBands);
	}

	@Override
	public Duration getDuration() {
		return Duration.between(start, end).minus(getIdleDuration());
	}

	@Override
	@JsonIgnore
	public List<TimeBand> getContainedBands() {
		ArrayList<TimeBand> containedBands = new ArrayList<>(nestedBands);
		containedBands.addAll(idleBands);
		return containedBands;
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


