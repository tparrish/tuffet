package uk.co.dubit.couch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Connection {

	public static final String CONFIRMATION_KEY = "ok";
	public static final String ALL_DATABASE_URL = "_all_dbs";
	
	public static final int DEFAULT_COUCH_PORT = 5984;
	
	private String hostName;
	private int port;
	
	public Connection(final String hostname) {
		this.hostName = hostname;
		this.port = DEFAULT_COUCH_PORT;
	}
	
	public Connection(final String hostname, final int port) {
		this.hostName = hostname;
		this.port = port;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public int getPort() {
		return port;
	}
	
	/**
	 * This is a 'simple' version of the get database call, which simply returns null if a problem is encountered retrieving the database.
	 * @param name the database name
	 * @return null or the database if the database is found
	 */
	public Database getOrCreateDatabase(final String name) throws RequestException {
		
		try {
			return getDatabase(name);
		} catch (RequestException e) {
			return createDatabase(name);
		}
	}
	
	public Database getDatabase(final String name) throws RequestException {
		final Request request = createRequest(name);
		
		final Response resp = request.get();
		
		return new Database(resp.getBodyAsMap(),this);
	}

	public Database createDatabase(final String name) throws RequestException {
		
		final Request request = createRequest(name);

		final Response resp = request.put();
		
		final Map<?,?> respObj = resp.getBodyAsMap();
		
		if(respObj.get(CONFIRMATION_KEY) instanceof Boolean && (Boolean)respObj.get(CONFIRMATION_KEY))
			return new Database(name,this);
		else
			throw new RequestException("Cannot create database successfully! "+resp.getBodyAsString());
	}
	
	public Collection<Database> getDatabases() throws RequestException {
		final Request request = createRequest(ALL_DATABASE_URL);
		
		final Response resp = request.get();
		final List<?> items = resp.getBodyAsList();
		
		final Collection<Database> dbs = new ArrayList<Database>(items.size());
		
		for(final Object id : items) {
			dbs.add(new Database((String)id,this));
		}
		
		return dbs;
	}
	
	protected ConnectionRequest createRequest(final String path) {
		return new ConnectionRequest(path);
	}
	
	class ConnectionRequest extends AbstractRequest {

		private String path;
		
		public ConnectionRequest(final String path) {
			this.path = path;
		}
		
		public String getUrl() {
			return "http://"+getHostName()+":"+getPort()+"/"+path;
		}
		
	}
}
