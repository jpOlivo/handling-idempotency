package com.demo.idempotency.api.service;

import java.util.List;

import com.demo.idempotency.api.model.StagedJobs;

public interface StagedJobService {
	StagedJobs save(StagedJobs stagedJob);

	List<StagedJobs> findFirst10(String jobName);

	void delete(List<StagedJobs> jobs);
}
