package com.demo.idempotency.fake.api.deposit.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositAccountDto {
	private String id;

	@NotBlank
	private String accountHolderKey;

	@NotBlank
	private String productTypeKey;

	@NotBlank
	private String accountHolderType;

	@NotBlank
	private String name;

	// ... see https://api.mambu.com/#tocsdepositaccount

}
