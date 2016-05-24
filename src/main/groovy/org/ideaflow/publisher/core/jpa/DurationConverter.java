package org.ideaflow.publisher.core.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

	@Override
	public Long convertToDatabaseColumn(Duration duration) {
		return duration == null ? null : duration.getSeconds();
	}

	@Override
	public Duration convertToEntityAttribute(Long durationInSeconds) {
		return durationInSeconds == null ? null : Duration.ofSeconds(durationInSeconds);
	}

}
