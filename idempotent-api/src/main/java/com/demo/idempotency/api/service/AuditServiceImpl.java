package com.demo.idempotency.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.api.model.AuditRecords;
import com.demo.idempotency.api.repository.AuditRepository;

@Service
public class AuditServiceImpl implements AuditService {
	@Autowired
	private AuditRepository auditRepository;

	@Override
	public AuditRecords save(AuditRecords audit) {
		return auditRepository.save(audit);
	}

}
