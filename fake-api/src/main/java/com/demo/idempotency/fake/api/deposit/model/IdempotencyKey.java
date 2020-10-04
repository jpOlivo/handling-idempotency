package com.demo.idempotency.fake.api.deposit.model;

import lombok.Data;

@Data
public class IdempotencyKey {
	private int responseCode;
	private String responseBody; 
}
