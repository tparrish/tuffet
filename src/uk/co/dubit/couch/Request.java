package uk.co.dubit.couch;


public interface Request {
	
	public String getUrl();
	
	public String addUrlParameter(final String name, final String value);
	
	public String removeUrlParameter(final String name);
	
	public Response post(final RequestBody body) throws RequestException;
	
	public Response put(final RequestBody body) throws RequestException;
	
	public Response post() throws RequestException;
	
	public Response put() throws RequestException;
	
	public Response delete() throws RequestException;
	
	public Response get() throws RequestException;
}
