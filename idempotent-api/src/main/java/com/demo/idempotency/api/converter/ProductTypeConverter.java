package com.demo.idempotency.api.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.demo.idempotency.api.enumeration.ProductType;

@Converter
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

	@Override
	public String convertToDatabaseColumn(ProductType type) {
		if (type == null) {
			return null;
		}
		return type.getName();
	}

	@Override
	public ProductType convertToEntityAttribute(String nameType) {
		if (nameType == null) {
			return null;
		}

		return Stream.of(ProductType.values()).filter(c -> c.getName().equals(nameType)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}