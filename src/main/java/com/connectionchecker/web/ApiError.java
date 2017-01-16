package com.connectionchecker.web;

import org.springframework.http.HttpStatus;
/**
 * Error wrapper to send errors back to clients in JSON format.
 */
public class ApiError {

	private HttpStatus status;
	
	private String message;

	public ApiError(HttpStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus(){
		return this.status;
	}

	public String getMessage(){
		return this.message;
	}
}