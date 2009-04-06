package uk.co.dubit.couch;

import java.util.HashMap;
import java.util.Map;

public abstract class View {
	
	public static final String MAP_FUNCTION_FIELD = "map";
	public static final String REDUCE_FUNCTION_FIELD = "reduce";
	
	private String mapFunction, reduceFunction;
	
	protected View(){}
	
	protected View(final String mapFunction, final String reduceFunction) {
		setMapFunction(mapFunction);
		setReduceFunction(reduceFunction);
	}
	
	public String getMapFunction() {
		return mapFunction;
	}

	public void setMapFunction(String mapFunction) {
		this.mapFunction = mapFunction;
	}

	public String getReduceFunction() {
		return reduceFunction;
	}

	public void setReduceFunction(String reduceFunction) {
		this.reduceFunction = reduceFunction;
	}
	
	/**
	 * Query this view with the default set of QueryOptions. If the view has been modified or if the document hasn't been saved yet then it will first save the document.
	 * @return an object representing the set of results
	 * @throws RequestException If there is a problem processing this query.
	 */
	public ViewResultSet query() throws RequestException {
		return query(QueryOptions.getDefaultInstance());
	}
	
	public abstract ViewResultSet query(final QueryOptions config) throws RequestException;

	protected abstract Database getDatabase();
	
	/**
	 * A view is valid if it has either a reduce function or a map function specified, or both. 
	 * @return true if valid, false if not
	 */
	protected boolean isValid() {
		return getMapFunction() != null || getReduceFunction() != null;
	}
	
	protected Map<String,Object> toMap() {
		
		final Map<String,Object> data = new HashMap<String,Object>();
		final String mapFunction = getMapFunction();
		final String reduceFunction = getReduceFunction();
		
		if(mapFunction != null)
			data.put(MAP_FUNCTION_FIELD,mapFunction);
		if(reduceFunction != null)
			data.put(REDUCE_FUNCTION_FIELD,reduceFunction);
		
		return data;
	}
}
