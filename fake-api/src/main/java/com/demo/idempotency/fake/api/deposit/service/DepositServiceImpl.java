package com.demo.idempotency.fake.api.deposit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.fake.api.deposit.dto.DepositAccountDto;
import com.demo.idempotency.fake.api.deposit.model.DepositAccount;
import com.demo.idempotency.fake.api.deposit.repository.DepositAccountRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DepositServiceImpl implements DepositService {
	@Autowired
	private DepositAccountRepository depositAccountRepository;

	@Override
	public DepositAccount save(DepositAccountDto dto) {
		log.debug("Building entity from {}", dto);

		DepositAccount entity = new DepositAccount();
		entity.setAccountHolderKey(dto.getAccountHolderKey());
		entity.setAccountHolderType(dto.getAccountHolderType());
		entity.setName(dto.getName());
		entity.setProductTypeKey(dto.getProductTypeKey());

		DepositAccount entityPersisted = depositAccountRepository.save(entity);
		log.debug("Entity persisted {}", entityPersisted);

		return entityPersisted;
	}

}
