package com.demo.idempotency.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.idempotency.api.model.StagedJobs;
import com.demo.idempotency.api.repository.StagedJobRepository;

@Service
public class StagedJobServiceImpl implements StagedJobService {
	@Autowired
	private StagedJobRepository stagedJobRepository;

	@Override
	public StagedJobs save(StagedJobs stagedJob) {
		return stagedJobRepository.save(stagedJob);
	}

	@Override
	public List<StagedJobs> findFirst10(String jobName) {
		return stagedJobRepository.findFirst10ByJobName(jobName);
	}

	@Override
	public void delete(List<StagedJobs> jobs) {
		stagedJobRepository.deleteAll(jobs);
	}

}
