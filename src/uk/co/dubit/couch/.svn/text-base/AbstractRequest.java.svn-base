package uk.co.dubit.couch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractRequest implements Request {

	private enum StatusCodeType {
		INFORMATION,
		SUCCESS,
		REDIRECTION,
		CLIENT_ERROR,
		SERVER_ERROR;
		
		public static StatusCodeType getStatusCodeType(final int code) {
			
			if(code >= 100 && code < 200) {
				return INFORMATION;
			}
			else if(code >= 200 && code < 300) {
				return SUCCESS;
			}
			else if(code >= 300 && code < 400) {
				return REDIRECTION;
			}
			else if(code >= 400 && code < 500) {
				return CLIENT_ERROR;
			}
			else if(code >= 500 && code < 600) {
				return SERVER_ERROR;
			}
			else {
				throw new IllegalArgumentException("Cannot parse status code "+code);
			}
		}
		
	}
	
	private Map<String,String> parameters;
	private HttpClient httpClient = new HttpClient();
	
	public AbstractRequest() {
		parameters = new HashMap<String,String>();
	}
	
	public Response get() throws RequestException {
		return invoke(new GetMethod(getUrl()));
	}
	
	public Response delete() throws RequestException {
		return invoke(new DeleteMethod(getUrl()));
	}
	
	public Response post() throws RequestException {
		return invoke(new PostMethod(getUrl()));
	}
	
	public Response put() throws RequestException {
		return invoke(new PutMethod(getUrl()));
	}
	
	public Response post(final RequestBody body) throws RequestException {
		return invokeWithBody(new PostMethod(getUrl()), body);
	}

	public Response put(final RequestBody body) throws RequestException {
		return invokeWithBody(new PutMethod(getUrl()),body);
	}
	
	protected Response invokeWithBody(final EntityEnclosingMethod httpMethod, final RequestBody body) throws RequestException {
		if(body != null) {
			RequestEntity entity;
			try {
				entity = new StringRequestEntity(body.getBody(),"application/json","UTF-8");
			  	httpMethod.setRequestEntity(entity);
			} catch (UnsupportedEncodingException e) {
				throw new RequestProcessingException("Failed to send request", e);
			}
		}
		
		return invoke(httpMethod);
	}
	
	protected Response invoke(final HttpMethod httpMethod) throws RequestException {
		
		final StringBuilder queryString = new StringBuilder();
		
		if(!parameters.isEmpty()) {
			
			for(final Map.Entry<String,String> param : parameters.entrySet()) {
				try {
					queryString.append(URLEncoder.encode(param.getKey(),"UTF-8"));
					queryString.append("=");
					queryString.append(URLEncoder.encode(param.getValue(),"UTF-8"));
					queryString.append("&");
				}
				catch (final UnsupportedEncodingException e){}
			}
			
			queryString.deleteCharAt(queryString.length()-1);
			httpMethod.setQueryString(queryString.toString());
		}
		
		try {
			httpClient.executeMethod(httpMethod);
		} catch (HttpException e) {
			throw new RequestProcessingException("Failed to invoke request",e);
		} catch (IOException e) {
			throw new RequestProcessingException("Failed to invoke request",e);
		}
		
		
		final int statusCode = httpMethod.getStatusCode();
		switch(StatusCodeType.getStatusCodeType(statusCode)) {
			
			case INFORMATION :
			case SUCCESS :
							try {
								
								return new BasicResponse(httpMethod.getResponseBodyAsString());
							} catch (IOException e) {
								throw new RequestProcessingException("Failed to read body of response",e);
							}
							finally {
								httpMethod.releaseConnection();
							}
			case SERVER_ERROR :
			case CLIENT_ERROR :
							try {
								final Response resp = new BasicResponse(httpMethod.getResponseBodyAsString());
								final Map<?,?> respData = resp.getBodyAsMap();
								throw new ResponseStatusCodeException((String)respData.get(BasicResponse.ERROR_CODE_FIELD), (String)respData.get(BasicResponse.ERROR_REASON_FIELD), statusCode);
							}
							catch (final IOException e) {
								throw new RequestProcessingException("Problem retrieving message from server response ",e);
							}
			case REDIRECTION : 
			default :		try {
								throw new UnknownResponseException("Server request failed for unknown reasons", httpMethod.getResponseBodyAsString());
							}
							catch (final IOException e) {
								throw new RequestProcessingException("Problem retrieving message from server response ",e);
							}			
		}
	}
	
	public String addUrlParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public String removeUrlParameter(String name) {
		return this.parameters.remove(name);
	}
	
	static class BasicResponse implements Response {

		protected static final String ERROR_CODE_FIELD = "error";
		protected static final String ERROR_REASON_FIELD = "reason";
		
		private static final ObjectMapper mapper = new ObjectMapper();
		
		private String body;
		
		public BasicResponse(final String body) {
			this.body = body;
		}
		
		public List<?> getBodyAsList() throws ResponseFormatException {
			
			return getBodyAsType(List.class);
		}

		public Map<?,?> getBodyAsMap() throws ResponseFormatException {
			
			return getBodyAsType(Map.class);
			
			
		}
		
		protected <T> T getBodyAsType(final Class<T> t) throws ResponseFormatException {

			try {
				
				return mapper.readValue(getBodyAsString(), t);
				
			}
			catch (JsonParseException e) {
				throw new ResponseFormatException("Cannot treat response as json", body, e);
			}
			catch (IOException e) {
				throw new ResponseFormatException("Cannot treat response as json", body, e);
			}
		}

		public String getBodyAsString() {
			return body;
		}		
	}
}
