package com.demo.idempotency.fake.api.cbu.dto;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.demo.idempotency.fake.api.cbu.converter.HashMapConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbuDto implements Serializable {
	private String id;

	@NotNull
	@Convert(converter = HashMapConverter.class)
	private Map<String, Object> jobArgs;
}
