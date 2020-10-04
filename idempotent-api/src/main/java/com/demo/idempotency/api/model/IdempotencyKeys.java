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

import com.demo.idempotency.api.converter.HashMapConverter;
import com.demo.idempotency.api.converter.RecoveryPointConverter;
import com.demo.idempotency.api.enumeration.RecoveryPoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IdempotencyKeys {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //TODO: strategy only for test, review it 
	private long id;

	@NotNull
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@NotNull
	@Size(max = 100)
	private String idempotencyKey;

	@NotNull
	private OffsetDateTime lastRunAt = OffsetDateTime.now();

	private OffsetDateTime lockedAt = OffsetDateTime.now();

	@NotNull
	@Size(max = 10)
	private String requestMethod;

	@NotNull
	@Convert(converter = HashMapConverter.class)
	private Map<String, Object> requestParams;

	@NotNull
	@Size(max = 100)
	private String requestPath;

	private int responseCode;
	private String responseBody; // TODO: see
									// https://thorben-janssen.com/persist-postgresqls-jsonb-data-type-hibernate/
	@NotNull
	@Size(max = 50)
	@Convert(converter = RecoveryPointConverter.class)
	private RecoveryPoint recoveryPoint;

	@NotNull
	private long userId;

}
