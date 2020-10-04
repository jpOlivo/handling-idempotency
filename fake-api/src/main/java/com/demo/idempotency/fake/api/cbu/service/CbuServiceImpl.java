package com.demo.idempotency.fake.api.cbu.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.fake.api.cbu.dto.CbuDto;
import com.demo.idempotency.fake.api.cbu.model.Cbu;
import com.demo.idempotency.fake.api.cbu.repository.CbuRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CbuServiceImpl implements CbuService {
	@Autowired
	private CbuRepository cbuRepository;

	@Override
	public Cbu save(CbuDto dto) {

		log.debug("Building entity from {}", dto);

		Map<String, Object> jobArgs = dto.getJobArgs();
		Cbu entity = new Cbu();

		entity.setAccountType((String) jobArgs.get("accountType"));
		entity.setCbu((String) jobArgs.get("cbu"));
		entity.setCurrency((String) jobArgs.get("currency"));
		entity.setOwnerCuit((String) jobArgs.get("ownerCuit"));
		entity.setOwnerName((String) jobArgs.get("ownerName"));
		entity.setOwnerType((String) jobArgs.get("ownerType"));

		Cbu entityPersisted = cbuRepository.save(entity);
		log.debug("Entity persisted {}", entityPersisted);

		return entityPersisted;
	}

}
