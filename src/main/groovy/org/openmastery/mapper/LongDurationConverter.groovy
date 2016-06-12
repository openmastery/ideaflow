package org.openmastery.mapper

import org.dozer.CustomConverter
import org.dozer.MappingException

import java.time.Duration


class LongDurationConverter implements CustomConverter {

	@Override
	Object convert(Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {
		if (source == null) {
			return null
		}

		if (destinationClass == Long.class && sourceClass == Duration.class) {
			return ((Duration) source).seconds
		} else if (destinationClass == Number.class && sourceClass == Long.class) {
			return Duration.ofSeconds((Long) source)
		} else {
			throw new MappingException("Converter LongDurationConverter used incorrectly, destination=${destination}, source=${source}")
		}
	}

}
