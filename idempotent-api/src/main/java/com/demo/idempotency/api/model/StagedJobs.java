package com.demo.idempotency.api.model;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.demo.idempotency.api.converter.HashMapConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StagedJobs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //TODO; strategy only for test, review it 
	private long id;

	@NotNull
	private String jobName;

	@NotNull
	@Convert(converter = HashMapConverter.class)
	private Map<String, Object> jobArgs;
}
