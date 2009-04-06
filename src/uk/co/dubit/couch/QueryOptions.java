package uk.co.dubit.couch;

import java.util.HashMap;
import java.util.Map;

public class QueryOptions {

	public static final String KEY_QUERY_STRING = "key";
	public static final String START_KEY_QUERY_STRING = "startkey";
	public static final String END_KEY_QUERY_STRING = "endkey";
	public static final String START_DOCUMENT_QUERY_STRING = "startkey_docid";
	public static final String END_DOCUMENT_QUERY_STRING = "endkey_docid";
	public static final String LIMIT_QUERY_STRING = "limit";
	public static final String UPDATE_QUERY_STRING = "update";
	public static final String DESCENDING_QUERY_STRING = "descending";
	public static final String SKIP_QUERY_STRING = "skip";
	public static final String GROUP_QUERY_STRING = "group";
	public static final String GROUP_LEVEL_QUERY_STRING = "group_level";
	
	public static enum Grouping {
		NONE(0),
		BY_KEY(1),
		ALL_RESULTS(2);
		
		private int level;
		
		private Grouping(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
	}
	
	public QueryOptions(){}
	
	private Map<String,String> params = new HashMap<String,String>();
	
	public void setKey(final Object val) { setJsonParameter(KEY_QUERY_STRING, val); }
	
	public void clearKey() { params.remove(KEY_QUERY_STRING); }
	
	public void setStartKey(final Object val) { setJsonParameter(START_KEY_QUERY_STRING, val); }
	
	public void clearStartKey() { params.remove(START_KEY_QUERY_STRING); }
	
	public void setEndKey(final Object val) { setJsonParameter(END_KEY_QUERY_STRING, val); }
	
	public void clearEndKey() { params.remove(END_KEY_QUERY_STRING); }
	
	private void setJsonParameter(final String key, final Object val) {
		if(val instanceof String)
			params.put(key, "\""+val+"\"");
		else if(val == null)
			params.remove(key);
		else
			params.put(key, val.toString());
	}
	
	public void setStartDocumentId(final String id) { params.put(START_DOCUMENT_QUERY_STRING, id); }
	
	public void clearStartDocumentId() { params.remove(START_DOCUMENT_QUERY_STRING); }
	
	public void setEndDocumentId(final String id) { params.put(END_DOCUMENT_QUERY_STRING, id); }
	
	public void clearEndDocumentId() { params.remove(END_DOCUMENT_QUERY_STRING); }
	
	public void setResultSize(final int size) { params.put(LIMIT_QUERY_STRING, Integer.toString(size)); }
	
	public void clearResultSize() { params.remove(LIMIT_QUERY_STRING); }
	
	public void setResultOffset(final int offset) { params.put(SKIP_QUERY_STRING, Integer.toString(offset)); }
	
	public void clearResultOffset() { params.remove(SKIP_QUERY_STRING); }
	
	public void setReIndex(final boolean reindex) {
		
		if(reindex)
			params.put(UPDATE_QUERY_STRING, "true");
		else
			params.remove(UPDATE_QUERY_STRING);
	}
	
	public void setDescendingOrder() {
		params.put(DESCENDING_QUERY_STRING, "true");
	}
	
	public void setAscendingOrder() {
		params.put(DESCENDING_QUERY_STRING, "false");
	}
	
	public void setGroupedBy(final Grouping group) {
		
		switch(group) {
			case NONE	:
						params.remove(GROUP_LEVEL_QUERY_STRING);
						params.remove(GROUP_QUERY_STRING);
						break;
			case ALL_RESULTS:
			case BY_KEY	:
						params.put(GROUP_QUERY_STRING, "true");
						params.put(GROUP_LEVEL_QUERY_STRING, Integer.toString(group.getLevel()));
						break;
			default		:
						throw new IllegalArgumentException("Cannot handle grouping type "+group);
		}
	}
	
	public Map<String,String> getUrlParameters() {
		return params;
	}
	
	public static QueryOptions getDefaultInstance() {
		return new QueryOptions();
	}
}
