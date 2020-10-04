package com.demo.idempotency.fake.api.deposit.service;

import com.demo.idempotency.fake.api.deposit.dto.DepositAccountDto;
import com.demo.idempotency.fake.api.deposit.model.DepositAccount;

public interface DepositService {
	DepositAccount save(DepositAccountDto dto);
}
