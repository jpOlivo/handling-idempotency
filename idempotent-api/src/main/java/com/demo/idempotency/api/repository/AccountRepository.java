package com.demo.idempotency.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.idempotency.api.model.Accounts;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {
	Accounts findByIdempotencyKeyId(long id);
}
