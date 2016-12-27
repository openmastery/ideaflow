package org.openmastery.publisher.config;

import com.bancvue.rest.config.ObjectMapperContextResolver;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.publisher.api.metrics.DurationInSeconds;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CustomValueTypeResolver  implements ContextResolver<ObjectMapper> {
	private static ObjectMapper mapper = new ObjectMapperContextResolver().getContext(null);

	static {
		mapper.registerModule(createCustomMetricValueModule());
	}

	private static SimpleModule createCustomMetricValueModule() {

		SimpleModule module = new SimpleModule();
		module.addSerializer(DurationInSeconds.class, new StdSerializer<DurationInSeconds>(DurationInSeconds.class) {
			@Override
			public void serialize(DurationInSeconds value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
				if (value == null) {
					jgen.writeNull();
				} else {
					jgen.writeNumber(value.getDurationInSeconds());
				}

			}
		});
		return module;
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}
