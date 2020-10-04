package com.demo.idempotency.fake.api.cbu.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Cbu {
	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private String ownerName;

	@Column(nullable = false)
	private String ownerCuit;

	@Column(nullable = false)
	private String ownerType;

	@Column(nullable = false)
	private String accountType;

	@Column(nullable = false)
	private String currency;

	@Column(nullable = false)
	private String cbu;

}
