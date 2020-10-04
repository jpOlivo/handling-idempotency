package com.demo.idempotency.fake.api.deposit.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class DepositAccount {
	@Id
	@GeneratedValue
	private UUID id;
	
	@Column(nullable = false)
	private String accountHolderKey;
	
	@Column(nullable = false)
	private String productTypeKey;
	
	@Column(nullable = false)
	private String accountHolderType;
	
	@Column(nullable = false)
	private String name;
	
	// ... see https://api.mambu.com/#tocsdepositaccount
	
}
