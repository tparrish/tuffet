package uk.co.dubit.couch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Database {

	public static final String NAME_RECORD_ENTRY = "db_name";
	public static final String ALL_DOCUMENTS_URL = "_all_docs";
	public static final String TOTAL_ROWS_FIELD = "total_rows";
	public static final String ROWS_FIELD = "rows";
	
	private String name;
	private Connection connection;
	
	protected Database(final String name, final Connection conn) {
		this.name = name;
		this.connection = conn;
	}
	
	protected Database(final Map<?,?> record, final Connection conn) {
		this.name = (String)record.get(NAME_RECORD_ENTRY);
		this.connection = conn;
		//XXX may want to record other details from database record here?
	}
	
	public AdHocView createAdHocView() {
		return new AdHocView(this);
	}
	
	public ViewDocument createViewDocument(final String view) {
		return new ViewDocument(view,this);
	}
	
	@SuppressWarnings("unchecked")
	public ViewDocument getViewDocument(final String view) throws RequestException {
		final Request request = createRequest(ViewDocument.DESIGN_NAMESPACE+view);
		final Response resp = request.get();
		return new ViewDocument((Map<String,Object>)resp.getBodyAsMap(), this);
	}
	
	public Document createDocument() {
		return new Document(this);
	}
	
	public Document createDocument(final String id) {
		return new Document(id,this);
	}
	
	@SuppressWarnings("unchecked")
	public Document getDocument(final String id) throws RequestException {
		
		final Request req = createRequest(id);
		
		final Response resp = req.get();
		
		try {
			final Map<String,Object> data = (Map<String, Object>) resp.getBodyAsMap();
			
			return new Document(data,this);
		} catch (ResponseFormatException e) {
			throw new IllegalArgumentException("Could not get all documents for this database from response "+resp.getBodyAsString());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Document> getAllDocuments() throws RequestException {
		
		final Request req = createRequest(ALL_DOCUMENTS_URL);
		
		final Response resp = req.get();
		
		final Map<String,Object> data = (Map<String, Object>) resp.getBodyAsMap();
		final int totalDocs = (Integer)data.get(TOTAL_ROWS_FIELD);
		
		final Collection<Document> docs = new ArrayList<Document>(totalDocs);
		final Collection<Map> rawDocs = (Collection<Map>)data.get(ROWS_FIELD);
		
		for(final Map rawDoc : rawDocs) {
			docs.add(Document.fromRowEntry(rawDoc,this));
		}
		
		return docs;
	}
	
	public void delete() throws RequestException {
		final Request req = createRequest("");
		
		req.delete();
	}
	
	public String getName() {
		return name;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	protected Request createRequest(final String path) {
		
		return getConnection().createRequest(getName()+"/"+path);
		
	}
	
	/**
	 * A small test app to check the operationality of the main functions.
	 * @param args
	 * @throws RequestException
	 */
	public static void main(final String[] args) throws RequestException {
		final Connection conn = new Connection("localhost");
		final Collection<Database> dbs = conn.getDatabases();
		
		for(final Database db : dbs) {
			System.out.println("Reading DB "+db.getName());
			for(final Document doc : db.getAllDocuments()) {
				System.out.println(db.getName()+" : "+db.getDocument(doc.getId()));
			}
		}
		
		Database db;
		
		try {
			db = conn.getDatabase("test_db");
		}
		catch(final ResponseStatusCodeException e) {
			db = conn.createDatabase("test_db");
		}			
		
		final Document doc = db.createDocument();
		doc.put("hello", "kitty");
		doc.put("win", 5000);
		doc.save();
		
		for(final Document dbDoc : db.getAllDocuments()) {
			System.out.println(db.getName()+" : "+db.getDocument(dbDoc.getId()));
		}
		
		final AdHocView view = db.createAdHocView();
		view.setMapFunction("function(doc) { if(doc.hello=='kitty') emit(doc._id, doc.hello); }");
		
		final ViewResultSet results = view.query();
		
		for(final ViewResult res : results.getResults()) {
			System.out.println("Match : "+res.getKey()+" : "+res.getValue()+" from "+res.getDocumentId());
		}
		
		doc.delete();
		
		System.out.println("Got document count of "+db.getAllDocuments().size());
		
		db.delete();
	}
}
