package com.buymeapie;

public class Error {

	// constant that defines internal server error
	public static final Integer ERROR_INTERNAL_SERVER = 1;

	// some other error code
	public static final Integer ERROR_SOME_OTHER = 2;

	@SuppressWarnings("unused")
	private Integer errorCode = null;
	@SuppressWarnings("unused")
	private String error = null;

	protected Error(Integer errorCode) {
		setError();
		this.errorCode = errorCode;
	}

	private void setError() {
		error = "true";
	};
};
