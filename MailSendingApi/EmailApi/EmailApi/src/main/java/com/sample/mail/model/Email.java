package com.sample.mail.model;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import jakarta.mail.Multipart;

public class Email {
	
	private int id;
	private String organisation;
	private String fromAddress;
	private String toAddresses;
	private String bccAddresses;
	private String ccAddresses;
	private String subject;
	private String body;
	private boolean fileAttached;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(String bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	public String getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isFileAttached() {
		return fileAttached;
	}

	public void setFileAttached(boolean fileAttached) {
		this.fileAttached = fileAttached;
	}

	public Email(int id, String organisation, String fromAddress, String toAddresses, String bccAddresses,
			String ccAddresses, String subject, String body, boolean fileAttached) {
		super();
		this.id = id;
		this.organisation = organisation;
		this.fromAddress = fromAddress;
		this.toAddresses = toAddresses;
		this.bccAddresses = bccAddresses;
		this.ccAddresses = ccAddresses;
		this.subject = subject;
		this.body = body;
		this.fileAttached = fileAttached;
	}

	public Email() {
		super();
	}

	@Override
	public String toString() {
		return "Email [id=" + id + ", organisation=" + organisation + ", fromAddress=" + fromAddress + ", toAddresses="
				+ toAddresses + ", bccAddresses=" + bccAddresses + ", ccAddresses=" + ccAddresses + ", subject="
				+ subject + ", body=" + body + ", fileAttached=" + fileAttached + "]";
	}

}
