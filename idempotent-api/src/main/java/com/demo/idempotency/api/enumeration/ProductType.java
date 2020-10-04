package com.demo.idempotency.api.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {
	CAS("CAS"), CAD("CAD");

	private String name;
}
