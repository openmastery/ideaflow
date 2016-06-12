package org.openmastery.mapper;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class Target {

	private String field;
	private String targetField;
	private Duration duration;
	private LocalDateTime localDateTime;
	private LocalDateTime convertJodaLocalDateTime;
	private org.joda.time.LocalDateTime convertJavaLocalDateTime;

}
