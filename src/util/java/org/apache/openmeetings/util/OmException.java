package org.apache.openmeetings.util;

public class OmException extends Exception {
	private static final long serialVersionUID = 1L;
	private Long code;
	
	public OmException(String msg) {
		super(msg);
	}
	
	public OmException(Long code) {
		this.code = code;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}
}
