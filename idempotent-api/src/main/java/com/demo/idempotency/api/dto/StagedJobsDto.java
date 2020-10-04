package com.demo.idempotency.api.dto;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.demo.idempotency.api.converter.HashMapConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StagedJobsDto implements Serializable {

	@NotNull
	@Convert(converter = HashMapConverter.class)
	private Map<String, Object> jobArgs;
}
