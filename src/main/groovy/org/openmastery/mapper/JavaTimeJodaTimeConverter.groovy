package org.openmastery.mapper

import org.dozer.CustomConverter
import org.dozer.MappingException
import org.openmastery.time.TimeConverter

import java.time.LocalDateTime

class JavaTimeJodaTimeConverter implements CustomConverter {

	@Override
	Object convert(Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {
		if (source == null) {
			return null
		}

		Object converted;
		if (destinationClass == org.joda.time.LocalDateTime.class && sourceClass == LocalDateTime.class) {
			converted = TimeConverter.toJodaLocalDateTime((LocalDateTime) source)
		} else if (destinationClass == LocalDateTime.class && sourceClass == org.joda.time.LocalDateTime.class) {
			converted = TimeConverter.toJavaLocalDateTime((org.joda.time.LocalDateTime) source)
		} else {
			throw new MappingException("Converter LongDurationConverter used incorrectly, destination=${destination}, source=${source}")
		}
		return converted;
	}

}
