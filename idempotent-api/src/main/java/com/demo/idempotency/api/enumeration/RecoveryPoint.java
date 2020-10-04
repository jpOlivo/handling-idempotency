package com.demo.idempotency.api.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecoveryPoint {
	RECOVERY_POINT_STARTED("started"), 
	RECOVERY_POINT_ACCOUNT_CREATED("account_created"),
	RECOVERY_POINT_DEPOSIT_CREATED("deposit_created"),
	RECOVERY_POINT_FINISHED("finished");
	
	private String name;
}
