/**
 * Copyright 2015 New Iron Group, Inc.
 * <p>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.time.Duration;
import java.time.LocalDateTime;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.registerModule(createJavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	private static JavaTimeModule createJavaTimeModule() {
		JavaTimeModule timeModule = new JavaTimeModule();
		timeModule.addSerializer(Duration.class, TimeSerializationSupport.DURATION_SERIALIZER);
		timeModule.addDeserializer(Duration.class, TimeSerializationSupport.DURATION_DESERIALIZER);
		timeModule.addKeyDeserializer(Duration.class, TimeSerializationSupport.DURATION_KEY_DESERIALIZER);
		timeModule.addSerializer(LocalDateTime.class, TimeSerializationSupport.LOCAL_DATE_TIME_SERIALIZER);
		return timeModule;
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}


}