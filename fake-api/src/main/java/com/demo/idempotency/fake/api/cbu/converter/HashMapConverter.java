package com.demo.idempotency.fake.api.cbu.converter;

import java.io.IOException;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Object> input) {

		String output = null;
		try {
			output = objectMapper.writeValueAsString(input);
		} catch (final JsonProcessingException e) {
			log.error("JSON writing error", e);
		}

		return output;
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String input) {

		Map<String, Object> output = null;
		try {
			output = objectMapper.readValue(input, Map.class);
		} catch (final IOException e) {
			log.error("JSON reading error", e);
		}

		return output;
	}

}
