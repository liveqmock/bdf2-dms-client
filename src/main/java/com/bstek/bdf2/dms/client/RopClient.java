package com.bstek.bdf2.dms.client;

import com.rop.RopRequest;

public abstract interface RopClient {
	public abstract <T> RopCompositeResponse<?> get(String paramString1,
			String paramString2, RopRequest paramRopRequest,
			Class<T> paramClass, String paramString3);

	public abstract <T> RopCompositeResponse<?> post(String paramString1,
			String paramString2, RopRequest paramRopRequest,
			Class<T> paramClass, String paramString3);
}