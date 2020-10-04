package com.demo.idempotency.api.service;

import com.demo.idempotency.api.model.Accounts;

public interface AccountService {
	Accounts findByIdempotencyKeyId(long id);

	Accounts save(Accounts account);
}
