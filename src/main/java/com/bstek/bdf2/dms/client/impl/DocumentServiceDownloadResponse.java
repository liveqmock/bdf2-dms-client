package com.bstek.bdf2.dms.client.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "documentServiceDownloadResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("restriction")
public class DocumentServiceDownloadResponse {

	@XmlAttribute
	private boolean processFlag;

	@XmlAttribute
	private String message;

	@XmlAttribute
	private String documentName;

	@XmlAttribute
	private byte[] content;

	public boolean isProcessFlag() {
		return this.processFlag;
	}

	public void setProcessFlag(boolean processFlag) {
		this.processFlag = processFlag;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDocumentName() {
		return this.documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}