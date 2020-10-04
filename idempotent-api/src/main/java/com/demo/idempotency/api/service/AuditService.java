package com.demo.idempotency.api.service;

import com.demo.idempotency.api.model.AuditRecords;

public interface AuditService {
	AuditRecords save(AuditRecords audit);
}
