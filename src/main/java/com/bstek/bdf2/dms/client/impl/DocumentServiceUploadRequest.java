package com.bstek.bdf2.dms.client.impl;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.bstek.bdf2.dms.client.AbstractDocumentServiceRequest;
import com.rop.annotation.IgnoreSign;
import com.rop.request.UploadFile;

public class DocumentServiceUploadRequest extends
		AbstractDocumentServiceRequest {

	@NotNull(message = "上传到文件夹路径参数不能为空")
	@Size(min = 2, max = 100, message = "上传到文件夹参数长度需在2~100之间")
	private String folderPath;

	@NotNull(message = "上传文件名参数不能为空")
	@Size(min = 3, max = 60, message = "上传文件名参数长度需在3~60之间")
	private String fileName;

	@IgnoreSign
	@NotNull(message = "上传内容不能为空。")
	private UploadFile uploadDocument;

	public UploadFile getUploadDocument() {
		return this.uploadDocument;
	}

	public void setUploadDocument(UploadFile uploadDocument) {
		this.uploadDocument = uploadDocument;
	}

	public String getFolderPath() {
		return this.folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}