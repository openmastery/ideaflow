package org.ideaflow.publisher.core.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleActivity {

	long id;

	LocalDateTime start;
	LocalDateTime end;

	String comment;

	boolean auto;
}
