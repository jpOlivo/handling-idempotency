package com.demo.idempotency.fake.api.cbu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.idempotency.fake.api.cbu.model.Cbu;

@Repository
public interface CbuRepository extends JpaRepository<Cbu, String> {

}
