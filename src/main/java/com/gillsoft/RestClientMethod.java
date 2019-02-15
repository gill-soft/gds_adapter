package com.gillsoft;

import java.util.Iterator;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;

public enum RestClientMethod {

	PING(RestClient.PING, "", null),
	LOCALES(RestClient.LOCALES, "", null),
	CURRENCIES(RestClient.CURRENCIES, "", null),
	COUNTRIES(RestClient.COUNTRIES, "", null),
	CITIES(RestClient.CITIES, "/locality/all", HttpMethod.GET),
	STATIONS(RestClient.STATIONS, "", null),
	TRIPS_INIT(RestClient.TRIPS, "/search", HttpMethod.POST),
	TRIPS(RestClient.TRIPS, "/search/%s", HttpMethod.GET),
	TARIFFS(null, "/search/trip/%s/tariffs/%s", HttpMethod.GET),
	REQUIRED_FIELDS(null, "/search/trip/%s/required", HttpMethod.GET),
	RULES(RestClient.RULES, "", null),
	ROUTE(RestClient.ROUTE, "/search/trip/%s/route/%s", HttpMethod.GET),
	SEATS_MAP(RestClient.SEATS_MAP, "/search/trip/%s/seats/scheme", HttpMethod.GET),
	FREE_SEATS(RestClient.FREE_SEATS, "/search/trip/%s/seats", HttpMethod.GET),
	NEW_ORDER(RestClient.NEW_ORDER, "/order", HttpMethod.POST),
	RESERVE(RestClient.RESERVE, "/order/%s/booking", HttpMethod.POST),
	BUY(RestClient.BUY, "/order/%s/confirm", HttpMethod.POST),
	PRINT(RestClient.PRINT, "/order/%s/document", HttpMethod.GET),
	INFO(RestClient.INFO, "/order/%s", HttpMethod.GET),
	CANCEL(RestClient.CANCEL, "/order/%s/cancel", HttpMethod.POST),
	ANNULMENT(RestClient.ANNULMENT, "", null),
	AUTO_RETURN(RestClient.AUTO_RETURN, "", null),
	RETURN(RestClient.RETURN, "", null),
	TICKET_AUTO_RETURN(RestClient.TICKET_AUTO_RETURN, "", null),
	TICKET_AUTO_RETURN_PRICE(RestClient.TICKET_AUTO_RETURN_PRICE, "", null),
	TICKET_ANNULMENT(RestClient.TICKET_ANNULMENT, "", null),
	TICKET_RETURN(RestClient.TICKET_RETURN, "", null),
	RETURN_PREPARE(null, "/order/return/prepare", HttpMethod.POST),
	RETURN_CONFIRM(null, "/order/return/confirm", HttpMethod.POST);

	private String matrixUrl;

	private String gdsUrl;
	
	private HttpMethod httpMethod;

	private RestClientMethod(final String matrixUrl, String gdsUrl, HttpMethod httpMethod) {
		this.matrixUrl = matrixUrl;
		this.gdsUrl = gdsUrl;
		this.httpMethod = httpMethod;
	}

	public String getMatrixUrl() {
		return matrixUrl;
	}

	public String getGdsUrl() {
		return gdsUrl;
	}

	public String getGdsUrl(String matrixUrl) {
		Iterator<RestClientMethod> iterator = Stream.of(RestClientMethod.values()).filter(f -> f.getMatrixUrl().equals(matrixUrl)).iterator();
		return iterator.hasNext() ? iterator.next().getGdsUrl() : "";
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

}
