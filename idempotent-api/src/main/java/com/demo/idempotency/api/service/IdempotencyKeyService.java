package com.demo.idempotency.api.service;

import com.demo.idempotency.api.model.IdempotencyKeys;

public interface IdempotencyKeyService {
	IdempotencyKeys findByKey(String key);

	IdempotencyKeys save(IdempotencyKeys idempotencyKeys);
}
