package uk.co.dubit.couch;

import java.io.IOException;
import java.util.Map;

public class AdHocView extends View {

	public static final String AD_HOC_VIEW_NAMESPACE = "_temp_view";
	
	private Database db;
	
	protected AdHocView(final Database db) {
		this.db = db;
	}
	
	@Override
	protected Database getDatabase() {
		return db;
	}

	@Override
	public ViewResultSet query(final QueryOptions config) throws RequestException {
		
		if(!isValid()) {
			throw new RequestProcessingException("Cannot process view, you must specify a map function or a reduce function");
		}
		
		final Database db = getDatabase();
		
		final Request req = db.createRequest(AD_HOC_VIEW_NAMESPACE);
		
		for(final Map.Entry<String, String> urlParam : config.getUrlParameters().entrySet()) {
			req.addUrlParameter(urlParam.getKey(), urlParam.getValue());
		}
		
		Response resp;
		try {
			resp = req.post(new MapRequestBody(toMap()));
		} catch (IOException e) {
			throw new RequestProcessingException("Could not form the ad hoc query correctly", e);
		}
		
		return ViewResultSet.fromViewQuery(resp.getBodyAsMap(),this);
	}

}
