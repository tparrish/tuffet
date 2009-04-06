package uk.co.dubit.couch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Document implements Map<String,Object> {
	
	public static final String ID_FIELD = "id";
	public static final String REVISION_FIELD = "rev";
	
	public static final String HIDDEN_ID_FIELD = "_id";
	public static final String HIDDEN_REVISION_FIELD = "_rev";
	
	public static final String ALL_REVISIONS_FIELD = "_all_revs";
	
	private String id, revision;
	private boolean isDeleted;
	private Database database;
	private Map<String,Object> data;
	
	protected Document(final Database db) {
		this.data = new HashMap<String,Object>();
		this.database = db;
	}
	
	protected Document(Map<String,Object> data, final Database db) {
		final Map<String,Object> docData = new HashMap<String,Object>(data);
		setId((String)docData.remove(HIDDEN_ID_FIELD));
		this.revision = (String)docData.remove(HIDDEN_REVISION_FIELD);
		this.data = docData;
		this.database = db;
	}
	
	protected Document(final String id, final Database db) {
		setId(id);
		this.data = new HashMap<String,Object>();
		this.database = db;
	}
	
	/**
	 * This constructor should be used when a document has been loaded from get all documents, but hasn't been populated with data.
	 * @param id The id of document that should be loaded
	 * @param revision The document revision that should be loaded
	 * @param db The database to load all this from
	 */
	protected Document(final String id, final String revision, final Database db) {
		setId(id);
		this.database = db;
		this.revision = revision;
	}
	
	/**
	 * This method should return all fields that should be manipulable as map data. the implementation of map operations in this class delegate to this method.
	 * If the data has not already been loaded then it is loaded on demand.
	 * @return A mutable map of data fields.
	 */
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getData() {
		if(data == null) {
			final Request req = createRequest();
			
			try {
				final Response resp = req.get();
				
				data = new HashMap<String,Object>((Map<String,Object>)resp.getBodyAsMap());
			}
			catch(final RequestException e) {
				throw new IllegalStateException("Cannot load data for document "+getId());
			}
		}
		
		return data;
	}
	
	/**
	 * This method should return all the 'non magical' attributes that a document should be persisted with. 
	 * It primarily exists to get around the quirk of View documents which at an object level have a seperation between attributes and views, but no distinction is made at the data level.
	 * @return A map of attributes
	 */
	protected Map<String,Object> getAttributes() {
		return getData();
	}
	
	public Database getDatabase() {
		return database;
	}
	
	protected void setId(final String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRevision() {
		return revision;
	}
	
	@SuppressWarnings("unchecked")
	public void setRevision(final String revision) throws RequestException {
		final Request req = createRequest();
		req.addUrlParameter(REVISION_FIELD, revision);
		
		final Response resp = req.get();
		
		Map<String, Object> data = new HashMap<String,Object>((Map<String,Object>)resp.getBodyAsMap());
		
		data.remove(HIDDEN_REVISION_FIELD);
		data.remove(HIDDEN_ID_FIELD);
		this.data = data;
		this.revision = revision;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRevisions() throws RequestException {
		
		final Request req = createRequest();
		req.addUrlParameter("revs", "true");
		
		final Response resp  = req.get();
		
		Map<String, Object> data = (Map<String, Object>) resp.getBodyAsMap();
		
		final List<Map> revs = (List<Map>) data.get(ALL_REVISIONS_FIELD);
		final List<String> allRevs = new ArrayList<String>(revs.size());
		
		for(final Map rev : revs) {
			allRevs.add((String)rev.get(HIDDEN_REVISION_FIELD));
		}
		
		return allRevs;
	}
	
	@SuppressWarnings("unchecked")
	public void save() throws RequestException {
		
		final String id = getId();
		if(id == null) {
			//Need to generate an id. This is a slightly different operation
			
			try {
				final Request req = database.createRequest("");
				final Response resp = req.post(new MapRequestBody(getAttributes()));
				
				final Map<String,Object> data = (Map<String,Object>)resp.getBodyAsMap();
				
				//If the update is successful then update the revision
				
				this.id = (String)data.get(ID_FIELD);
				this.revision = (String)data.get(REVISION_FIELD);
			}
			catch (final IOException e) {
				throw new RequestProcessingException("Failed to encode body",e);
			}			
		}
		else {
			//Save the document and increment the version number
			
			try {
				final Request req = createRequest();
				
				final Map<String,Object> requestData = new HashMap<String,Object>(getAttributes());
				final Object revision = getRevision();
				if(revision != null)
					requestData.put(HIDDEN_REVISION_FIELD, revision);
				final Response resp = req.put(new MapRequestBody(requestData));
				
				final Map<String,Object> data = (Map<String,Object>)resp.getBodyAsMap();
				
				//If the update is successful then update the revision
				
				this.revision = (String)data.get(REVISION_FIELD);
			}
			catch (final IOException e) {
				throw new RequestProcessingException("Failed to encode body",e);
			}
			catch (final ResponseFormatException e) {
				throw new RequestProcessingException("Failed to read response",e);
			}
		}
		
	}
	
	/**
	 * Deletes the document from the underlying database. This method is not thread safe!
	 * @throws RequestException if the document can't be found for deletion
	 * @return true if the record was deleted, false if it has already been deleted or was never saved.
	 */
	public boolean delete() throws RequestException {
		
		if(isDeleted)
			return false;//Has already been deleted.
		else if(this.revision == null)
			return false;//Hasn't been saved.
		
		final Request req = createRequest();
		req.addUrlParameter(REVISION_FIELD, getRevision());
		
		req.delete();//If a problem occurs there will be a request exception thrown here
		
		isDeleted = true;
		
		return true;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	protected Request createRequest() {
		return database.createRequest(getId());
	}
	
	public void clear() {
		getData().clear();
	}

	public boolean containsKey(Object key) {
		return getData().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return getData().containsValue(value);
	}

	public Set<Entry<String,Object>> entrySet() {
		return getData().entrySet();
	}
	
	public Object get(Object key) {
		return getData().get(key);
	}

	public boolean isEmpty() {
		return getData().isEmpty();
	}

	public Set<String> keySet() {
		return getData().keySet();
	}

	public Object put(String key, Object value) {
		return getData().put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> t) {
		getData().putAll(t);
	}

	public Object remove(Object key) {
		return getData().remove(key);
	}

	public int size() {
		return getData().size();
	}

	public Collection<Object> values() {
		return getData().values();
	}

	public String toString() {
		
		final Map<String,Object> rep = new HashMap<String, Object>();
		
		rep.put("id", getId());
		rep.put("rev", getRevision());
		rep.put("data", getData().toString());
		
		return rep.toString();
	}

	protected static Document fromRowEntry(Map<?,?> rawDoc, final Database db) {
		final Map<?,?> vals = (Map<?,?>)rawDoc.get("value");
		
		return new Document((String)rawDoc.get(ID_FIELD), (String)vals.get(REVISION_FIELD), db);
	}
}
