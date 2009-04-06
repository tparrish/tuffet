package uk.co.dubit.couch;

public class ResponseFormatException extends RequestException {

	private static final long serialVersionUID = 2915449348853684919L;
	private String body;

	public ResponseFormatException(final String message, final String body, final Throwable t) {
		super(message,t);
		this.body = body;
	}
	
	public ResponseFormatException(final String message, final String body) {
		super(message);
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}

}
