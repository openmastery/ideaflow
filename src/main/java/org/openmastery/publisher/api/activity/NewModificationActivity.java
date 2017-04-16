package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewModificationActivity implements NewActivity {

	private Long taskId;
	private Long durationInSeconds;
	private int modificationCount;

	@NotNull
	private LocalDateTime endTime;

}
