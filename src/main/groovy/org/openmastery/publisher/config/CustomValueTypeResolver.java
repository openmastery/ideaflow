/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.openmastery.publisher.api.metrics.CapacityDistribution;
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

		module.addSerializer(CapacityDistribution.class, new StdSerializer<CapacityDistribution>(CapacityDistribution.class) {
			@Override
			public void serialize(CapacityDistribution value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
				if (value == null) {
					jgen.writeNull();
				} else {
					jgen.writeObject(value.getCapacityDistributionByType());
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
