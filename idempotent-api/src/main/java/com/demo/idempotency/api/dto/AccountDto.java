package com.demo.idempotency.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AccountDto {
	@NotNull
	private String accountHolderId;
	@NotNull
	private String productTypeId;

	// TODO: move to Owner entity
	@NotNull
	private String ownerName;

	@NotNull
	@Size(min = 13, max = 13)
	private String ownerCuit;

	@NotNull
	private String ownerType; // TODO: add javax-validations-enums, see:
								// https://www.baeldung.com/javax-validations-enums

	@NotNull
	@Size(min = 22, max = 22)
	private String cbu;

	@NotNull
	private String accountType; // TODO: add javax-validations-enums, see:
								// https://www.baeldung.com/javax-validations-enums

	@NotNull
	private String currency; // TODO: add javax-validations-enums, see:
								// https://www.baeldung.com/javax-validations-enums

}
