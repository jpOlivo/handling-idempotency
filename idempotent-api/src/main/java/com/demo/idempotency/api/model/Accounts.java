package com.demo.idempotency.api.model;

import java.time.OffsetDateTime;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.demo.idempotency.api.converter.ProductTypeConverter;
import com.demo.idempotency.api.enumeration.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Accounts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //TODO: strategy only for test, review it 
	private long id;

	@NotNull
	private OffsetDateTime createdAt = OffsetDateTime.now();

	private long idempotencyKeyId; // TODO: add relation

	@NotNull
	private String accountHolderId;

	@NotNull
	@Convert(converter = ProductTypeConverter.class)
	private ProductType productTypeId;

	@Size(max = 50)
	private String depositAccountId;

	// @Size(max = 50)
	// private String cbuAccountId;

	@NotNull
	private long userId; // TODO: add relation

}
