package org.openmastery.mapper;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class Source {

	private String field;
	private String sourceField;
	private Duration duration;
	private LocalDateTime localDateTime;
	private LocalDateTime convertJavaLocalDateTime;
	private org.joda.time.LocalDateTime convertJodaLocalDateTime;

}
