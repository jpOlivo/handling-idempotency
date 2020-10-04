package com.demo.idempotency.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Users {
	@Id
	private long id;

	@NotNull
	@Email()
	@Size(max = 255)
	private String email;

	@NotNull
	@Size(max = 50)
	private String mambuCustomerId;
}
