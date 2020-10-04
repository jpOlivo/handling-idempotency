package com.demo.idempotency.api.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.demo.idempotency.api.enumeration.RecoveryPoint;

@Converter
public class RecoveryPointConverter implements AttributeConverter<RecoveryPoint, String> {

	@Override
	public String convertToDatabaseColumn(RecoveryPoint recoveryPoint) {
		if (recoveryPoint == null) {
			return null;
		}
		return recoveryPoint.getName();
	}

	@Override
	public RecoveryPoint convertToEntityAttribute(String name) {
		if (name == null) {
			return null;
		}

		return Stream.of(RecoveryPoint.values()).filter(c -> c.getName().equals(name)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}