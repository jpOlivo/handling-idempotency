package com.demo.idempotency.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.api.model.IdempotencyKeys;
import com.demo.idempotency.api.repository.IdempotencyKeyRepository;

@Service
public class IdempotencyKeyServiceImpl implements IdempotencyKeyService {
	@Autowired
	private IdempotencyKeyRepository idempotencyKeyRepository;

	@Override
	public IdempotencyKeys findByKey(String key) {
		return idempotencyKeyRepository.findByIdempotencyKey(key);
	}

	@Override
	public IdempotencyKeys save(IdempotencyKeys idempotencyKeys) {
		return idempotencyKeyRepository.save(idempotencyKeys);
	}

}
