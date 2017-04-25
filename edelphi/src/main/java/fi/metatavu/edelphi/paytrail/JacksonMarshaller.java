package fi.metatavu.edelphi.paytrail;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonMarshaller implements fi.metatavu.paytrail.json.Marshaller {

	public JacksonMarshaller() {
	  createObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	@Override
	public String objectToString(Object object) throws IOException {
		return createObjectMapper().writeValueAsString(object);
	}
	
	@Override
	public <T> T stringToObject(Class<? extends T> clazz, String string) throws IOException {
		return createObjectMapper().readValue(string, clazz);
	}
	
	private ObjectMapper createObjectMapper() {
	  return new ObjectMapper();
	}

}
