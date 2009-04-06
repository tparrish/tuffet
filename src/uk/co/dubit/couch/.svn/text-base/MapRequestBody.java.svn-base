package uk.co.dubit.couch;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class MapRequestBody implements RequestBody {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	private String body;
	
	public MapRequestBody(final Map<String,Object> data) throws IOException {
		
		final StringWriter writer = new StringWriter();
		
		mapper.writeValue(writer, data);
		
		writer.flush();
		
		this.body = writer.toString();
	}
	
	public String getBody() {
		return body;
	}

}
