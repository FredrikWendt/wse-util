package se.wendt.util;


public class WseException extends RuntimeException {

	/**
	 * serialization.
	 */
	private static final long serialVersionUID = 8767218418880577188L;

	public WseException(String message, Throwable cause) {
		super(message, cause);
	}

	public WseException(Throwable cause) {
		super(cause);
	}

	public WseException(String message) {
		super(message);
	}

}
