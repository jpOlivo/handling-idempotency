package com.demo.idempotency.api.action;

/**
 * Represents an action to perform
 */
public interface Action {
	void execute(String key);
}
