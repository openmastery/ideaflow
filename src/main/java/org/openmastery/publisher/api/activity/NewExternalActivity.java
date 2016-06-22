package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewExternalActivity {

	private Long taskId;
	private Long durationInSeconds;
	private String comment;

}
