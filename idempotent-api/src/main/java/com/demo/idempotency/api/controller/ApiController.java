package com.demo.idempotency.api.controller;

import static com.demo.idempotency.api.constant.Constants.CREATE_CBU_JOB_NAME;
import static com.demo.idempotency.api.constant.Constants.IDEMPOTENCY_KEY_LOCK_TIMEOUT;
import static com.demo.idempotency.api.constant.Constants.RESOURCE_TYPE_ACCOUNT;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Status;

import com.demo.idempotency.api.action.NoOpActionImpl;
import com.demo.idempotency.api.action.RecoveryPointActionImpl;
import com.demo.idempotency.api.action.ResponseActionImpl;
import com.demo.idempotency.api.dto.AccountDto;
import com.demo.idempotency.api.dto.DepositAccountDto;
import com.demo.idempotency.api.enumeration.AuditEvent;
import com.demo.idempotency.api.enumeration.ProductType;
import com.demo.idempotency.api.enumeration.RecoveryPoint;
import com.demo.idempotency.api.exception.ApiProblem;
import com.demo.idempotency.api.model.Accounts;
import com.demo.idempotency.api.model.AuditRecords;
import com.demo.idempotency.api.model.IdempotencyKeys;
import com.demo.idempotency.api.model.StagedJobs;
import com.demo.idempotency.api.service.AccountService;
import com.demo.idempotency.api.service.AuditService;
import com.demo.idempotency.api.service.IdempotencyKeyService;
import com.demo.idempotency.api.service.StagedJobService;
import com.demo.idempotency.api.service.StateMutationService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ApiController {


	@Autowired
	private IdempotencyKeyService idempotencyKeyService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private StateMutationService stateMutationService;

	@Autowired
	private StagedJobService stagedJobService;

	@Autowired
	private WebClient webClient;

	// @Value("${account-deposit.timeout:30}")
	// private long timeoutAccountDepositApi;

	@PostMapping("/account")
	public ResponseEntity<String> createAccount(@RequestBody AccountDto accountDto,
			@RequestHeader("Idempotency-Key") String key, HttpServletRequest request) {
		log.info("[headers]: Idempotency-Key = {}", key);
		log.info("[body]: {}", accountDto);

		// Our first atomic phase to create or update an idempotency key.
		//
		// A key concept here is that if two requests try to insert or update within
		// close proximity, one of the two will be aborted by Postgres because we're
		// using a transaction with SERIALIZABLE isolation level. It may not look
		// it, but this code is safe from races.
		stateMutationService.mutate(key, () -> {
			log.info("tx1 -> create/update idempotency key");
			// TODO: Find by key and user
			IdempotencyKeys idempotencyKey = idempotencyKeyService.findByKey(key);

			if (idempotencyKey != null) {
				log.info("IdempotencyKeys {} from DB", idempotencyKey);
				
				// TODO: review validations:
				
				// Programs sending multiple requests with different parameters but the same idempotency key is a bug.
				if (!idempotencyKey.getRequestParams().equals(getInputParameters(accountDto))) {
					throw new ResponseStatusException(HttpStatus.CONFLICT,
							"There was a mismatch between this request's parameters and the parameters of a previously stored request with the same Idempotency-Key.");
				}
				// Only acquire a lock if the key is unlocked or its lock as expired because it was long enough ago.
		        if(idempotencyKey.getLockedAt().isAfter(OffsetDateTime.now().minusSeconds(IDEMPOTENCY_KEY_LOCK_TIMEOUT))) {
		        	throw new ResponseStatusException(HttpStatus.CONFLICT, "An API request with the same Idempotency-Key is already in progress.");
		        }

				// Lock the key and update latest run unless the request is already
				// finished.
				if (!RecoveryPoint.RECOVERY_POINT_FINISHED.equals(idempotencyKey.getRecoveryPoint())) {
					log.info("Updating IdempotencyKeys {} in DB for recovery point {}", key,
							idempotencyKey.getRecoveryPoint());
					OffsetDateTime now = OffsetDateTime.now();
					idempotencyKey.setLastRunAt(now);
					idempotencyKey.setLockedAt(now);
					idempotencyKey = idempotencyKeyService.save(idempotencyKey);
					log.debug("IdempotencyKeys updated in DB {}", idempotencyKey);
				}

			} else {
				log.info("IdempotencyKeys {} is not present in DB, creating a new entry", idempotencyKey);
				idempotencyKey = idempotencyKeyService.save(new IdempotencyKeys().toBuilder().idempotencyKey(key)
						.recoveryPoint(RecoveryPoint.RECOVERY_POINT_STARTED).requestMethod(request.getMethod())
						.requestPath(request.getRequestURI()).requestParams(getInputParameters(accountDto)).userId(1) // TODO: remove hardcode user 
						.build());
				log.debug("IdempotencyKeys persisted in DB {}", idempotencyKey);
			}

			// no response and no need to set a recovery point
			return new NoOpActionImpl();
		});

		// walking across of DAG. TODO: to recursive function?
		log.debug("Starting to walk through DAG for IdempotencyKeys {}", key);
		boolean walk = true;
		while (walk) {
			IdempotencyKeys idempotencyKey = idempotencyKeyService.findByKey(key);

			switch (idempotencyKey.getRecoveryPoint()) {
			case RECOVERY_POINT_STARTED:
				log.info("DAG step {}", RecoveryPoint.RECOVERY_POINT_STARTED);
				stateMutationService.mutate(key, () -> {
					// local state mutations
					log.info("tx2 -> create account");
					Accounts account = accountService
							.save(new Accounts().toBuilder().accountHolderId(accountDto.getAccountHolderId())
									.productTypeId(ProductType.valueOf(accountDto.getProductTypeId()))
									.idempotencyKeyId(idempotencyKey.getId()).userId(1) // TODO: remove hardcode user
									.build());
					
					log.info("tx2 -> create account audit record");
					auditService.save(new AuditRecords().toBuilder().event(AuditEvent.ACCOUNT_CREATED)
							.data(getInputParameters(accountDto)).originIp(request.getLocalAddr())
							.resourceId(account.getId()).resourceType(RESOURCE_TYPE_ACCOUNT)
							.userId(1) // TODO: remove hardcode user
							.build());

					// Creating new recovery point
					return new RecoveryPointActionImpl(RecoveryPoint.RECOVERY_POINT_ACCOUNT_CREATED.getName());
				});
				break;

			case RECOVERY_POINT_ACCOUNT_CREATED:
				log.info("DAG step {}", RecoveryPoint.RECOVERY_POINT_ACCOUNT_CREATED);
				stateMutationService.mutate(key, () -> {
					// retrieve a account record if necessary (i.e. we're recovering)
					Accounts account = accountService.findByIdempotencyKeyId(idempotencyKey.getId());
					// TODO: throw new Exception: if account is still nil by this point, we have a bug

					try {
						// foreign state mutations
						log.info("tx3 -> create deposit account");
						DepositAccountDto depositAccountDto = this.webClient.post().uri("/deposits")
								.header("Idempotency-Key", "api-".concat(key))
								.body(BodyInserters.fromValue(
										DepositAccountDto.builder()
												.accountHolderKey(accountDto.getAccountHolderId())
												.productTypeKey(accountDto.getProductTypeId())
												.accountHolderType("CLIENT") // TODO: dummy value
												.name("myDepositAccount") // TODO: dummy value
												.build()))

								.retrieve()
								.onStatus(HttpStatus::is4xxClientError, response -> {
									log.warn("DepositAccount not updated due a client error");
									return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
											"4xx error on /deposits external endpoint"));
								})
								.onStatus(HttpStatus::is5xxServerError, response -> {
									log.warn("DepositAccount not updated due a server error");
									return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
											"5xx error on /deposits external endpoint"));
								})
								.bodyToMono(DepositAccountDto.class)
								.timeout(Duration.ofSeconds(5)) // TODO: use only for test, it simulate network issues using it with debug mode
								.block(); // TODO: only for test, move to asynchronous
						log.debug("DepositAccount updated through external service");
						
						// local state mutations
						log.info("tx3 -> update account");
						account.setDepositAccountId(depositAccountDto.getId());
						accountService.save(account);
						log.debug("Account updated in DB");

						// Creating new recovery point
						return new RecoveryPointActionImpl(RecoveryPoint.RECOVERY_POINT_DEPOSIT_CREATED.getName());

					} catch (ResponseStatusException e) {
						// Mark this request as error and returning the response
						log.error(e.getReason(), e.getCause());
						return new ResponseActionImpl(e.getStatus(), e.getReason());
					}
				});
				break;

			case RECOVERY_POINT_DEPOSIT_CREATED:
				log.info("DAG step {}", RecoveryPoint.RECOVERY_POINT_DEPOSIT_CREATED);
				stateMutationService.mutate(key, () -> {
					// foreign state mutations (deferred)
					log.info("tx4 -> create cbu job (to run deferred)");
					
					// building args for call to external service
					Map<String, Object> args = new HashMap<>();
					args.put("idempotencyKey", key);
					args.put("ownerName", accountDto.getOwnerName());
					args.put("ownerCuit", accountDto.getOwnerCuit());
					args.put("ownerType", accountDto.getOwnerType());
					args.put("accountType", accountDto.getAccountType());
					args.put("currency", accountDto.getCurrency());
					args.put("cbu", accountDto.getCbu());

					// creating new job in order to run in background 
					StagedJobs stagedJob = StagedJobs.builder().jobName(CREATE_CBU_JOB_NAME).jobArgs(args).build();
					stagedJobService.save(stagedJob);
					log.debug("StagedJob created in DB");

					// Mark this request as success and returning the response
					return new ResponseActionImpl(HttpStatus.CREATED, "Account created successfully");
				});
				break;

			case RECOVERY_POINT_FINISHED:
				log.info("DAG step {}", RecoveryPoint.RECOVERY_POINT_FINISHED);
				walk = false;
				break;

			default:
				throw new IllegalStateException(
						String.format("Bug! Unhandled recovery point %s'", idempotencyKey.getRecoveryPoint()));
			}
		}
		log.debug("Ending to walk through DAG for IdempotencyKeys {}", key);
		

		// TODO: improve it!
		IdempotencyKeys idempotencyKey = idempotencyKeyService.findByKey(key);
		int responseCode = idempotencyKey.getResponseCode();
		String responseBody = idempotencyKey.getResponseBody();
		log.info("Request/Response lifecycle for IdempotencyKeys {} finished with HTTP status code {}", responseCode);
		
		if (responseCode > 201) {
			log.warn("Raising exception for notify error on response");
			throw new ApiProblem(Status.valueOf(responseCode), responseBody);
		}

		return new ResponseEntity<String>(responseBody, HttpStatus.resolve(responseCode));

	}

	private Map<String, Object> getInputParameters(AccountDto accountDto) {
		Map<String, Object> requestParams = new HashMap<>();
		
		requestParams.put("accountHolderId", accountDto.getAccountHolderId());
		requestParams.put("productTypeId", accountDto.getProductTypeId());
		requestParams.put("ownerName", accountDto.getOwnerName());
		requestParams.put("ownerCuit", accountDto.getOwnerCuit());
		requestParams.put("ownerType", accountDto.getOwnerType());
		requestParams.put("accountType", accountDto.getAccountType());
		requestParams.put("currency", accountDto.getCurrency());
		requestParams.put("cbu", accountDto.getCbu());
		
		return requestParams;
	}

}
