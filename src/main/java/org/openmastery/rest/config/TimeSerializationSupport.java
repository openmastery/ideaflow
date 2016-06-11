package org.openmastery.rest.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class TimeSerializationSupport {

	public static final StdSerializer<Duration> DURATION_SERIALIZER = new CustomDurationSerializer();
	public static final StdDeserializer<Duration> DURATION_DESERIALIZER = new CustomDurationDeserializer();
	public static final KeyDeserializer DURATION_KEY_DESERIALIZER = new CustomDurationKeyDeserializer();
	public static final LocalDateTimeSerializer LOCAL_DATE_TIME_SERIALIZER = new LocalDateTimeSerializer(
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
	);

	private static class CustomDurationSerializer extends DurationSerializer {

		protected CustomDurationSerializer() {
			super(DurationSerializer.INSTANCE, null, null);
		}

		@Override
		public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {
			if (useTimestamp(provider)) {
				super.serialize(duration, generator, provider);
			} else {
				generator.writeNumber(duration.getSeconds());
			}
		}
	}

	private static class CustomDurationDeserializer extends StdScalarDeserializer<Duration> {

		protected CustomDurationDeserializer() {
			super(Duration.class);
		}

		@Override
		public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
			if (parser.getCurrentTokenId() == JsonTokenId.ID_NUMBER_INT) {
				return Duration.ofSeconds(parser.getLongValue());
			}
			throw context.mappingException("Expected type float, integer, or string.");
		}

	}

	private static class CustomDurationKeyDeserializer extends KeyDeserializer {

		@Override
		public final Object deserializeKey(String key, DeserializationContext ctxt) {
			if (Jsr310NullKeySerializer.NULL_KEY.equals(key)) {
				return null;
			}
			return Duration.ofSeconds(Long.parseLong(key));
		}

	}

}
