package com.demo.idempotency.fake.api.cbu.model;

import lombok.Data;

@Data
public class IdempotencyKey {
	private int responseCode;
	private String responseBody;
}
