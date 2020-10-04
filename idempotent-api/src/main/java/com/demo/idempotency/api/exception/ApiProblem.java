package com.demo.idempotency.api.exception;

import java.net.URI;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import lombok.Data;

@Data
public class ApiProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/api-problem");
    
    private Status status;
    
    private String data;

    public ApiProblem(Status status, String data) {
    	super(TYPE, "Houston, we have a problem", status);
    	this.status = status;
    	this.data = data;
    }

}