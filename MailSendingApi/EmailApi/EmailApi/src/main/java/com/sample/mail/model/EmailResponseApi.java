package com.sample.mail.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmailResponseApi {
	SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String respdatetime;
	private int status;
	private String satatusdesc;
	private List<Email> email;

	public String getRespdatetime() {
		return dateTimeFormatter.format(new Date());
	}

	public void setRespdatetime(String respdatetime) {
		this.respdatetime = getRespdatetime();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSatatusdesc() {
		return satatusdesc;
	}

	public void setSatatusdesc(String satatusdesc) {
		this.satatusdesc = satatusdesc;
	}

	public List<Email> getEmail() {
		return email;
	}

	public void setEmail(List<Email> email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "EmailResponseApi [respdatetime=" + getRespdatetime() + ", status=" + status + ", satatusdesc=" + satatusdesc
				+ ", email=" + email + "]";
	}

	public EmailResponseApi(int status, String satatusdesc, List<Email> email) {
		super();
		this.respdatetime = getRespdatetime();
		this.status = status;
		this.satatusdesc = satatusdesc;
		this.email = email;
	}

	public EmailResponseApi() {
		super();
	}


}
