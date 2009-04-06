package uk.co.dubit.couch;

/**
 * This class is thrown when a 400 series code is returned from the server. It indicates that the user made a mistake in the request they sent
 * @author Thomas Williams on behalf of dubitlimited.com
 *
 */
public class ResponseStatusCodeException extends RequestException {

	private static final long serialVersionUID = -8389819844786260732L;
	private String code;
	private int httpStatusCode;
	
	public ResponseStatusCodeException(final String code, final String message, final int statusCode) {
		super(message);
		this.code = code;
		this.httpStatusCode = statusCode;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
