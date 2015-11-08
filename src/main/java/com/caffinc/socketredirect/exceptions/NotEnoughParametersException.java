package com.caffinc.socketredirect.exceptions;

/**
 * Exception thrown when the program is not started with enough/correct parameters
 * 
 * @author Testware
 */
public class NotEnoughParametersException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 609543368869751685L;

	public NotEnoughParametersException() {
		super();
	}

	public NotEnoughParametersException(String message) {
		super(message);
	}

	public NotEnoughParametersException(Throwable t) {
		super(t);
	}

	public NotEnoughParametersException(String message, Throwable t) {
		super(message, t);
	}
}
