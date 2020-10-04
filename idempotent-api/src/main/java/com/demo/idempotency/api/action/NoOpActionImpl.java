package com.demo.idempotency.api.action;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents an action to perform no operation
 */
@Slf4j
public class NoOpActionImpl implements Action {

	@Override
	public void execute(String key) {
		// empty implementation
		log.info("No operation performed");
	}

}
