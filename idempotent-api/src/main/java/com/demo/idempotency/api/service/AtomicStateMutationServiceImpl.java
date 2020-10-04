package com.demo.idempotency.api.service;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.idempotency.api.action.Action;
import com.demo.idempotency.api.model.IdempotencyKeys;
import com.demo.idempotency.api.repository.IdempotencyKeyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AtomicStateMutationServiceImpl implements StateMutationService {
	
	@Autowired
	private IdempotencyKeyRepository idempotencyKeyRepository;

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE) //TODO: see REQUIRED vs NESTED? && REPEATABLE_READ vs SERIALIZABLE
	@Override
	public void mutate(String key, Supplier<Action> function) {
		boolean error = false;
		
		try  {
			// executes a state mutation
			log.info("Performing a new state mutation on key {}", key);
			Action action = function.get();
			
			// updates request life cycle state
			log.info("Updating request life cycle state for key {}", key);
			action.execute(key);
		} catch (Exception e) {
			// TODO: handle exception
			error = true; 
			//throw new ApiProblem(Status.CONFLICT,
			//		"test");
			log.error("An error has occurred while trying perform state mutation. A rollback will be executed to remain a consistent state");
			throw new RuntimeException(e.getMessage(), e);
			
		} finally {
			//TODO: review is necessary?
			
			// If we're leaving under an error condition, try to unlock the idempotency
		    // key right away so that another request can try again.
			if(error && key != null) {
				log.info("Unlocking IdempotencyKeys on DB");
				IdempotencyKeys idempotencyKey = idempotencyKeyRepository.findByIdempotencyKey(key);
				idempotencyKey.setLockedAt(null);
				idempotencyKeyRepository.save(idempotencyKey);
				
				// We're already inside an error condition, so swallow any additional
		        // errors from here and just send them to logs.
				
		        // TODO: loguear error: "Failed to unlock key #{key.id}."
			}
		}

	}

}
