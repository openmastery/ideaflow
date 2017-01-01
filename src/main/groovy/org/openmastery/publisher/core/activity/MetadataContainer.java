/**
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.publisher.core.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class MetadataContainer {

	private HashMap container = new HashMap();
	private ObjectMapper mapper = new ObjectMapper();

	public void setMetadataField(String key, Object value) {
		container.put(key, value);
	}

	public boolean getMetadataValueAsBoolean(String key) {
		Object value = container.get(key);
		return value != null ? (Boolean) value : false;
	}

	public int getMetadataValueAsInteger(String key) {
		Object value = container.get(key);
		return value != null ? (Integer) value : Integer.MIN_VALUE;
	}

	public String getMetadataValue(String key) {
		Object value = container.get(key);
		return value != null ? value.toString() : null;
	}

	public void fromJson(String metadata) {
		try {
			container = toMetadataMap(metadata);
		} catch (IOException ex) {
			log.error("Failed to convert metadata into map, json=" + metadata, ex);
		}
	}

	private HashMap toMetadataMap(String metadata) throws IOException {
		if (metadata == null) {
			return new HashMap();
		} else {
			return mapper.readValue(metadata, HashMap.class);
		}
	}

	public String toJson() {
		try {
			return mapper.writeValueAsString(container);
		} catch (JsonProcessingException ex) {
			log.error("Failed to convert metadata into json, map=" + container, ex);
			return "";
		}
	}

}
