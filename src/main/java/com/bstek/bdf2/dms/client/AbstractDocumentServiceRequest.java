package com.bstek.bdf2.dms.client;

import com.rop.AbstractRopRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public abstract class AbstractDocumentServiceRequest extends AbstractRopRequest {
	public static final String METHOD_1_UPLOAD_ = "bdf2.dms.upload";
	public static final String METHOD_VERSION_1_ = "1.0";
	public static final String METHOD_2_DOWNLOAD_ = "bdf2.dms.download";
	public static final String METHOD_3_DELETE_ = "bdf2.dms.delete";

	@NotNull(message = "用户名参数不能为空")
	@Size(min = 3, max = 60, message = "用户名参数长度需在3~60之间")
	private String userName;

	@NotNull(message = "用户密码参数不能为空")
	@Size(min = 3, max = 60, message = "用户密码参数长度需在3~60之间")
	private String password;

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}