package com.demo.idempotency.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.api.model.Accounts;
import com.demo.idempotency.api.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Accounts save(Accounts account) {
		return accountRepository.save(account);
	}

	@Override
	public Accounts findByIdempotencyKeyId(long id) {
		return accountRepository.findByIdempotencyKeyId(id);
	}

}
