package uk.co.dubit.couch;

import java.util.HashMap;
import java.util.Map;

import uk.co.dubit.couch.QueryOptions.Grouping;

/**
 * A view document encapsulates a set of grouped views. They all reside under the namespace provided by this document. This document can act as a
 * traditional document but can additionally store a list of views.
 * @author Thomas Williams on behalf of dubitlimited.com
 *
 */
public class ViewDocument extends Document {

	public static final String DESIGN_NAMESPACE = "_design/";
	
	public static final String VIEW_DATA_FIELD = "views";
	
	public static final String LANGUAGE_FIELD = "language";
	
	private Map<String,View> views;
	private String name;
	
	protected ViewDocument(final Map<String,Object> rep, final Database db) {
		super(rep, db);
		
		extractViews();
	}
	
	protected ViewDocument(final String id, Database db) {
		super(DESIGN_NAMESPACE+id, db);
		
		views = new HashMap<String,View>();
	}
	
	@SuppressWarnings("unchecked")
	private void extractViews() {
		
		final Object viewData = remove(VIEW_DATA_FIELD);
		views = new HashMap<String,View>();
		
		if(viewData instanceof Map) {
			for(final Map.Entry<String,Object> viewItem : ((Map<String,Object>)viewData).entrySet()) {
				views.put(viewItem.getKey(), new StoredView(viewItem.getKey(),(Map<String,Object>)viewItem.getValue()));
			}
		}
	}
	
	protected void setId(final String id) {
		this.name = id.substring(DESIGN_NAMESPACE.length());
	}
	
	public String getId() {
		return DESIGN_NAMESPACE+name;
	}
	
	protected String getViewName() {
		return name;
	}
	
	public void setRevision(final String revision) throws RequestException {
		super.setRevision(revision);
		extractViews();
	}
	
	public String getLanguage() {
		return (String)get(LANGUAGE_FIELD);
	}
	
	public void setLanguage(final String lang) {
		put(LANGUAGE_FIELD, lang);
	}

	public View getView(final String name) {
		return views.get(name);
	}
	
	public View createView(final String name) {
		
		final View newView = new StoredView(name,this);
		
		if(views.containsKey(name)) {
			throw new IllegalArgumentException("View "+name+" already exists");
		}
		else {
			views.put(name, newView);
			return newView;
		}
	}
	
	protected Map<String,Object> getAttributes() {
		final Map<String,Object> viewData = new HashMap<String,Object>();
		
		for(final Map.Entry<String, View> viewEntry : views.entrySet()) {
			final View thisView = viewEntry.getValue();
			
			if(thisView.isValid())
				viewData.put(viewEntry.getKey(), thisView.toMap());
		}
		
		Map<String,Object> myData = new HashMap<String,Object>(getData());
		
		myData.put(VIEW_DATA_FIELD, viewData);
		
		return myData; 
	}
	
	protected class StoredView extends View {
		
		private String name;
		private boolean dirty;
		
		protected StoredView(final String name, final Map<String,Object> data) {
			super((String)data.get(View.MAP_FUNCTION_FIELD), (String)data.get(View.REDUCE_FUNCTION_FIELD));
			this.name = name;
			setDirty(false);
		}
		
		protected StoredView(final String name, final ViewDocument viewDocument) {
			this.name = name;
			setDirty(true);
		}
		
		public String getName() {
			return name;
		}
		
		public Database getDatabase() {
			return ViewDocument.this.getDatabase();
		}
		
		public void setMapFunction(String mapFunction) {
			//XXX do some checking here to make sure this function matches up with the specified language?
			super.setMapFunction(mapFunction);
			setDirty(true);
		}
		
		public void setReduceFunction(String reduceFunction) {
			//XXX do some checking here to make sure this function matches up with the specified language?
			super.setReduceFunction(reduceFunction);
			setDirty(true);
		}
		
		protected void setDirty(final boolean isDirty) {
			this.dirty = isDirty;
		}
		
		protected boolean isDirty() {
			return dirty;
		}
		
		public ViewResultSet query(final QueryOptions config) throws RequestException {
			
			if(!isValid()) {
				throw new RequestProcessingException("Cannot process view, you must specify a map function or a reduce function");
			}
			else if(isDirty()) {
				ViewDocument.this.save();
			}
			
			final Database db = getDatabase();
			
			final Request req = db.createRequest(DESIGN_NAMESPACE+ViewDocument.this.getViewName()+"/_view/"+getName());
			
			for(final Map.Entry<String, String> urlParam : config.getUrlParameters().entrySet()) {
				req.addUrlParameter(urlParam.getKey(), urlParam.getValue());
			}
			
			final Response resp = req.get();
			
			return ViewResultSet.fromViewQuery(resp.getBodyAsMap(),this);
		}
	}
	
	public static void main(final String[] args) throws RequestException {
		final Connection conn = new Connection("localhost",5984);
		final Database db = conn.getDatabase("nexus_testing");
		ViewDocument viewDoc = db.getViewDocument("nexusPersistenceView");
		
		View roomWithAView = viewDoc.getView("room_type");
//		roomWithAView.setMapFunction("function(doc) { if(doc._room_type) { emit(doc._room_type,doc) }}");
//		
//		roomWithAView = viewDoc.getView("room_type");
		
		viewDoc.save();
		
		final QueryOptions opts = new QueryOptions();
		opts.setKey("world");
		opts.setGroupedBy(Grouping.BY_KEY);
		roomWithAView.query(opts);
	}
}
