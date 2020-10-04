package com.demo.idempotency.api.model;

import java.time.OffsetDateTime;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.demo.idempotency.api.converter.AuditEventConverter;
import com.demo.idempotency.api.converter.HashMapConverter;
import com.demo.idempotency.api.enumeration.AuditEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AuditRecords {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //TODO: strategy only for test, review it 
	private long id;

	@NotNull
	@Size(max = 50)
	@Convert(converter = AuditEventConverter.class)
	private AuditEvent event;

	@NotNull
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@NotNull
	@Convert(converter = HashMapConverter.class)
	private Map<String, Object> data;

	@NotNull
	private String originIp;

	@NotNull
	private long resourceId;

	@NotNull
	@Size(max = 50)
	private String resourceType;

	@NotNull
	private long userId; // TODO: add relation
}
