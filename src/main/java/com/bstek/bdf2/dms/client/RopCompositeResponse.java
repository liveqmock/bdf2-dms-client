package com.bstek.bdf2.dms.client;

import com.rop.response.ErrorResponse;

public abstract interface RopCompositeResponse<T> {
	public abstract boolean isSuccess();

	public abstract void setErrorResponse(ErrorResponse paramErrorResponse);

	public abstract void setSuccessfulResponse(T paramT);

	public abstract ErrorResponse getErrorResponse();

	public abstract T getSuccessfulResponse();
}