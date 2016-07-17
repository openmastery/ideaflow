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
