package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewExternalActivity implements NewActivity {

	private Long taskId;
	private Long durationInSeconds;
	private String comment;

	@NotNull
	private LocalDateTime endTime;

}
