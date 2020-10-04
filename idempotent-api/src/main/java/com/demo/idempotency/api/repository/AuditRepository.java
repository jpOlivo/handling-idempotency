package com.demo.idempotency.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.idempotency.api.model.AuditRecords;

@Repository
public interface AuditRepository extends JpaRepository<AuditRecords, Long> {

}
