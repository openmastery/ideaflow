package org.ideaflow.publisher.api;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleActivity {

	private Duration duration;
	private String comment;
	private boolean auto;

}
