package com.demo.idempotency.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.idempotency.api.model.StagedJobs;

public interface StagedJobRepository extends JpaRepository<StagedJobs, Long> {
	List<StagedJobs> findFirst10ByJobName(String jobName);
}
