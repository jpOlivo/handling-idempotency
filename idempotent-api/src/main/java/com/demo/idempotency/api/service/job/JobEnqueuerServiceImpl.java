package com.demo.idempotency.api.service.job;

import static com.demo.idempotency.api.constant.Constants.BACKGROUND_JOBS_QUEUE;
import static com.demo.idempotency.api.constant.Constants.CREATE_CBU_JOB_NAME;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.idempotency.api.dto.StagedJobsDto;
import com.demo.idempotency.api.model.StagedJobs;
import com.demo.idempotency.api.service.StagedJobService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JobEnqueuerServiceImpl implements JobEnqueuerService {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private StagedJobService stagedJobService;
	
	//private static final int BATCH_SIZE = 1000;
	
	@Scheduled(fixedRate = 10000)
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public int enque() {
		List<StagedJobs> jobs = stagedJobService.findFirst10(CREATE_CBU_JOB_NAME);
		int sizeJobs = jobs.size();

		log.info("queuing {} new jobs in {}", sizeJobs, BACKGROUND_JOBS_QUEUE);
		for (StagedJobs stagedJobs : jobs) {
			StagedJobsDto dto = StagedJobsDto.builder().jobArgs(stagedJobs.getJobArgs()).build();
			rabbitTemplate.convertAndSend(BACKGROUND_JOBS_QUEUE, dto);
		}
		
		if(sizeJobs > 0) {
			stagedJobService.delete(jobs);
			log.info("Removing enqueued jobs from DB (StagedJobs)");
		}
		
		return sizeJobs;

	}
}
