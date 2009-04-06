package uk.co.dubit.couch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewResultSet {

	public static final String TOTAL_ROWS_FIELD = "total_rows";
	public static final String OFFSET_FIELD = "offset";
	public static final String ROWS_FIELD = "rows";
	
	private int offset;
	private List<ViewResult> results;
	private View view;
	
	protected ViewResultSet(final int offset, final List<Map<?,?>> results, final View view) {
		this.offset = offset;
		this.view = view;
		this.results = extractResults(results);
	}
	
	private List<ViewResult> extractResults(final List<Map<?,?>> results) {
		
		final List<ViewResult> resultObjs = new ArrayList<ViewResult>(results.size());
		
		for(final Map<?,?> result : results) {
			resultObjs.add(ViewResult.fromResultSet(result,this));
		}
		
		return resultObjs;
	}
	
	public View getView() {
		return view;
	}

	public int getRowCount() {
		return results.size();
	}

	public int getOffset() {
		return offset;
	}

	public List<ViewResult> getResults() {
		return results;
	}

	@SuppressWarnings("unchecked")
	protected static ViewResultSet fromViewQuery(final Map<?,?> data, final View view) {
		
		final int offset = data.containsKey(OFFSET_FIELD) ? (Integer)data.get(OFFSET_FIELD) : 0;
		final List<Map<?,?>> results = (List<Map<?,?>>)data.get(ROWS_FIELD);
		
		return new ViewResultSet(offset, results, view);
	}
	
}
