package com.demo.idempotency.api.service.job;

import com.demo.idempotency.api.dto.StagedJobsDto;

public interface JobDequeuerService {
	void dequeue(StagedJobsDto stagedJobsDto);
}
