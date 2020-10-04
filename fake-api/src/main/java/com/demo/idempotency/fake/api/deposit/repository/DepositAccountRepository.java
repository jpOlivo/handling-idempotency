package com.demo.idempotency.fake.api.deposit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.idempotency.fake.api.deposit.model.DepositAccount;

@Repository
public interface DepositAccountRepository extends JpaRepository<DepositAccount, String> {

}
