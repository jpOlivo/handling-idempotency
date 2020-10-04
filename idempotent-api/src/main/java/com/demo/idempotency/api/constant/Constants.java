package com.demo.idempotency.api.constant;

public final class Constants {
	private Constants() {
	}

	public static final String CREATE_CBU_JOB_NAME = "create_cbu";

	public static final String RESOURCE_TYPE_ACCOUNT = "account";

	public static final String BACKGROUND_JOBS_QUEUE = "backgroundJobsQueue";

	// Number of seconds passed which we consider a held idempotency key lock to be
	// defunct and eligible to be locked again by a different API call. We try to
	// unlock keys on our various failure conditions, but software is buggy, and
	// this might not happen 100% of the time, so this is a hedge against it.
	public static final long IDEMPOTENCY_KEY_LOCK_TIMEOUT = 90;
}