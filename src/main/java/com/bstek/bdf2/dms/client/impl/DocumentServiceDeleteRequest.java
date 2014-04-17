package com.bstek.bdf2.dms.client.impl;

import com.bstek.bdf2.dms.client.AbstractDocumentServiceRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DocumentServiceDeleteRequest extends
		AbstractDocumentServiceRequest {

	@NotNull(message = "要删除的文档UUID参数不能为空")
	@Size(min = 30, max = 60, message = "要删除的文档UUID参数长度需在30~60之间")
	private String documentUUID;

	public String getDocumentUUID() {
		return this.documentUUID;
	}

	public void setDocumentUUID(String documentUUID) {
		this.documentUUID = documentUUID;
	}
}