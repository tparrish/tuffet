package uk.co.dubit.couch;

import java.util.Map;

public class ViewResult {

	private static final String KEY_FIELD = "key";
	private static final String VALUE_FIELD = "value";
	private static final String DOCUMENT_ID_FIELD = "id";
	
	private ViewResultSet resultSet;
	private Object key, value;
	private String documentId;

	public ViewResult(Object key, Object value, String documentId, ViewResultSet resultSet) {
		this.key = key;
		this.value = value;
		this.documentId = documentId;
		this.resultSet = resultSet;
	}

	public ViewResultSet getResultSet() {
		return resultSet;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public String getDocumentId() {
		return documentId;
	}
	
	public Document getDocument() throws RequestException {
		final Database db = resultSet.getView().getDatabase();
		
		return db.getDocument(getDocumentId());
	}
	
	protected static ViewResult fromResultSet(final Map<?,?> data, final ViewResultSet resultSet) {
		
		final Object key = data.get(KEY_FIELD);
		final Object value = data.get(VALUE_FIELD);
		final String documentId = (String)data.get(DOCUMENT_ID_FIELD);
		
		return new ViewResult(key,value,documentId,resultSet);
	}
	
	public String toString() {
		return "key: "+getKey()+", value: "+getValue()+" document: "+getDocumentId();
	}
}
