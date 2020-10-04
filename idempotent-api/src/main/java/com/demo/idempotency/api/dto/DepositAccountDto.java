package com.demo.idempotency.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositAccountDto {
	private String id;
	private String accountHolderKey;
	private String productTypeKey;
	private String accountHolderType;
	private String name;

	// ... see https://api.mambu.com/#tocsdepositaccount

}
