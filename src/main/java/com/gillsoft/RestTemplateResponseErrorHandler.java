package com.gillsoft;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gillsoft.model.RestError;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	private static final ObjectReader reader = new ObjectMapper().readerFor(RestError.class);

	@Override
	public void handleError(ClientHttpResponse arg0) throws IOException {
		byte[] body = new byte[arg0.getBody().available()];
		arg0.getBody().read(body, 0, body.length);
		throw new IOException(new String(body));
	}

	@Override
	public boolean hasError(ClientHttpResponse arg0) throws IOException {
		return arg0.getStatusCode() != HttpStatus.OK;
	}

	public static RestError getRestError(String message) {
		try {
			return reader.readValue(message);
		} catch (Exception e) {
			return null;
		}
	}

}
