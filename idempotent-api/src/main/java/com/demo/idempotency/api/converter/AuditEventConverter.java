package com.demo.idempotency.api.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.demo.idempotency.api.enumeration.AuditEvent;

@Converter
public class AuditEventConverter implements AttributeConverter<AuditEvent, String> {

	@Override
	public String convertToDatabaseColumn(AuditEvent auditEvent) {
		if (auditEvent == null) {
			return null;
		}
		return auditEvent.getName();
	}

	@Override
	public AuditEvent convertToEntityAttribute(String name) {
		if (name == null) {
			return null;
		}

		return Stream.of(AuditEvent.values()).filter(c -> c.getName().equals(name)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}