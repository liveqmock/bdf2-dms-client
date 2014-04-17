package com.bstek.bdf2.dms.client.impl;

import com.bstek.bdf2.dms.client.RopCompositeResponse;
import com.rop.response.ErrorResponse;

public class DefaultRopCompositeResponse<T> implements RopCompositeResponse<T> {
	private boolean success;
	private ErrorResponse errorResponse;
	private T successfulResponse;

	DefaultRopCompositeResponse(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public ErrorResponse getErrorResponse() {
		return this.errorResponse;
	}

	public T getSuccessfulResponse() {
		return this.successfulResponse;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public void setSuccessfulResponse(T successfulResponse) {
		this.successfulResponse = successfulResponse;
	}
}