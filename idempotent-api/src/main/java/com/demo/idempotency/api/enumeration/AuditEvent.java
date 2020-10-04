package com.demo.idempotency.api.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuditEvent {
	ACCOUNT_CREATED("account.created");

	private String name;
}
