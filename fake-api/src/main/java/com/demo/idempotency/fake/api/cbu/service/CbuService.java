package com.demo.idempotency.fake.api.cbu.service;

import com.demo.idempotency.fake.api.cbu.dto.CbuDto;
import com.demo.idempotency.fake.api.cbu.model.Cbu;

public interface CbuService {
	Cbu save(CbuDto dto);
}
