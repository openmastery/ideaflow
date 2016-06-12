package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowBand {

	private long id;
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;
	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	private String startingComment;
	private String endingComent;

	private IdeaFlowStateType type;

	private List<IdeaFlowBand> nestedBands = new ArrayList<IdeaFlowBand>();


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


