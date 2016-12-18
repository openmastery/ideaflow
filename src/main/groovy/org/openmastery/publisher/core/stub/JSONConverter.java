package org.openmastery.publisher.core.stub;

import com.bancvue.rest.config.ObjectMapperContextResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.api.batch.NewBatchEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONConverter {

	Map<String, Class> idToClassMap = createIdToClassMap();

	Map<Class, String> classToIdMap = createClassToIdMap();

	ObjectMapper mapper;

	JSONConverter() {
		mapper = new ObjectMapperContextResolver().getContext(null);
	}

	private Map<String, Class> createIdToClassMap() {
		Map<String, Class> idToClassMap = new HashMap<String, Class>();
		idToClassMap.put("EditorActivity", NewEditorActivity.class);
		idToClassMap.put("ExecutionActivity", NewExecutionActivity.class);
		idToClassMap.put("ExternalActivity", NewExternalActivity.class);
		idToClassMap.put("ModificationActivity", NewModificationActivity.class);
		idToClassMap.put("IdleActivity", NewIdleActivity.class);
		idToClassMap.put("BlockActivity", NewBlockActivity.class);
		idToClassMap.put("Event", NewBatchEvent.class);
		return idToClassMap;
	}

	private Map<Class, String> createClassToIdMap() {
		Map<Class, String> classToIdMap = new HashMap<Class, String>();
		for (Map.Entry<String, Class> entry : createIdToClassMap().entrySet()) {
			classToIdMap.put(entry.getValue(), entry.getKey());
		}
		return classToIdMap;
	}

	String toJSON(Object object) throws JsonProcessingException {
		String typeName = classToIdMap.get(object.getClass());
		if (typeName == null) {
			throw new UnsupportedObjectType("Unable to find typeName for "+object.getClass().getName());
		}
		return typeName + "=" + mapper.writeValueAsString(object);
	}

	Object fromJSON(String jsonInString) throws IOException {
		String[] jsonSplit = jsonInString.split("=");
		String typeName = jsonSplit[0];
		String jsonContent = jsonSplit[1];
		Class clazz = idToClassMap.get(typeName);
		return mapper.readValue(jsonContent, clazz);
	}

	static class UnsupportedObjectType extends RuntimeException {

		UnsupportedObjectType(String message) {
			super(message);
		}
	}
}
