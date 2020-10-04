package com.demo.idempotency.api.service;

import java.util.function.Supplier;

import com.demo.idempotency.api.action.Action;

public interface StateMutationService {
	void mutate(String key, Supplier<Action> function);
}
