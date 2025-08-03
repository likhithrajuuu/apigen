package com.likhithraju.apigen.coder;

public class CoderException extends Throwable {
	private static final long serialVersionUID = 1L;

	public CoderException() {
		super();
	}
	
	public CoderException(String message) {
		super(message);
	}

	public CoderException(Throwable throwable) {
		super(throwable);
	}

	public CoderException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
