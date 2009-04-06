package uk.co.dubit.couch;

/**
 * This is the superclass for all types of exception that can be thrown from a request
 * @author Thomas Williams on behalf of dubitlimited.com
 *
 */
public class RequestException extends Exception {

	private static final long serialVersionUID = 3104503788535669379L;

	protected RequestException(String message, Throwable cause) {
		super(message,cause);
	}

	protected RequestException(String message) {
		super(message);
	}
}
