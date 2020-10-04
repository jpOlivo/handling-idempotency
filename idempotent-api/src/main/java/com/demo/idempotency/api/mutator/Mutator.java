package com.demo.idempotency.api.mutator;

import com.demo.idempotency.api.action.Action;

@Deprecated
@FunctionalInterface
public interface Mutator {
	Action execute();
}
