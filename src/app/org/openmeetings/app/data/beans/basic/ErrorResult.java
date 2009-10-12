package org.openmeetings.app.data.beans.basic;

public class ErrorResult {
	
	private Long errorId;
	private String errmessage;
	private String errortype;

	public ErrorResult(Long errorId, String errmessage, String errortype) {
		super();
		this.errorId = errorId;
		this.errmessage = errmessage;
		this.errortype = errortype;
	}

	public Long getErrorId() {
		return errorId;
	}

	public void setErrorId(Long errorId) {
		this.errorId = errorId;
	}

	public String getErrmessage() {
		return errmessage;
	}

	public void setErrmessage(String errmessage) {
		this.errmessage = errmessage;
	}

	public String getErrortype() {
		return errortype;
	}

	public void setErrortype(String errortype) {
		this.errortype = errortype;
	}

}
