package com.demo.idempotency.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.idempotency.api.model.IdempotencyKeys;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeys, Long> {
	IdempotencyKeys findByIdempotencyKey(String key);
}
