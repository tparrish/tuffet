package uk.co.dubit.couch;

/**
 * This exception is thrown if the response from the server is unknown, or a redirect.
 * @author Thomas Williams on behalf of dubitlimited.com
 *
 */
public class UnknownResponseException extends RequestException {

	private static final long serialVersionUID = -6139568025569283157L;
	
	private String body;
	
	public UnknownResponseException(String message, final String body) {
		super(message);
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
}
