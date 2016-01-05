package com.store59.box.exception;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {
	private int status;
	private String msg;

	public ServiceException() {
		this(-1);
	}

	public ServiceException(int status) {
		this(status, null);
	}

	public ServiceException(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		if (msg != null) {
			return msg;
		} else {
			return getMessage();
		}
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
