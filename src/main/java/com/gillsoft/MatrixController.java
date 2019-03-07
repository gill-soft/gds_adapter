package com.gillsoft;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.matrix.model.Locale;
import com.gillsoft.matrix.model.Order;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.ReturnRule;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.matrix.model.Seat;
import com.gillsoft.matrix.model.Station;
import com.gillsoft.matrix.model.Ticket;
import com.gillsoft.matrix.model.Trip;

@RestController
public class MatrixController {

	@Autowired
	private RestClient client;

	@GetMapping(RestClient.PING)
	public ResponseEntity<Response<Object>> ping(String login, String password, String locale) {
		//return client.ping(login, password);
		throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
	}

	@GetMapping(RestClient.LOCALES)
	public ResponseEntity<Response<Map<String, Locale>>> getLocales(String login, String password, String locale) {
		return client.getLocales(login, password, locale);
	}

	@GetMapping(RestClient.CURRENCIES)
	public ResponseEntity<Response<Map<String, String>>> getCurrencies(String login, String password, String locale) {
		return client.getCurrencies(login, password, locale);
	}

	@GetMapping(RestClient.COUNTRIES)
	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale) {
		return client.getCountries(login, password, locale, true);
	}

	@GetMapping(RestClient.CITIES)
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale,
			@RequestParam(name = "country_id", required = false) String countryId) {
		return client.getCities(login, password, locale, countryId, true);
	}

	@PostMapping(RestClient.TRIPS)
	public ResponseEntity<Response<List<Trip>>> getTrips(HttpServletRequest request) {
		return client.getTrips(request, true);
	}

	@PostMapping(RestClient.RULES)
	public ResponseEntity<Response<List<ReturnRule>>> getReturnRules(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return client.getReturnRules(login, password, locale, intervalId);
	}

	@PostMapping(RestClient.ROUTE)
	public ResponseEntity<Response<RouteInfo>> getRoute(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "route_id", required = false) String routeId) {
		return client.getRoute(login, password, locale, routeId);
	}

	@PostMapping(RestClient.SEATS_MAP)
	public ResponseEntity<Object> getSeatsMap(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return new ResponseEntity<>(client.getSeatsMap(login, password, locale, intervalId), HttpStatus.OK);
	}

	@PostMapping(RestClient.FREE_SEATS)
	public ResponseEntity<Response<Map<String, String>>> getFreeSeats(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return new ResponseEntity<>(client.getFreeSeats(login, password, locale, intervalId), HttpStatus.OK);
	}

	@PostMapping(RestClient.NEW_ORDER)
	public ResponseEntity<Response<Order>> create(HttpServletRequest request) {
		return client.create(request);
	}

	@PostMapping(RestClient.RESERVE)
	public ResponseEntity<Response<Order>> reserve(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		return client.reserve(login, password, locale, orderId);
	}

	@PostMapping(RestClient.BUY)
	public ResponseEntity<Response<Order>> buy(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		// выкупаем заказ у ресурса
		client.buy(login, password, locale, orderId);
		// формируем ссылку для вызова метода print
		String url = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
		url = url.substring(0, url.indexOf(RestClient.BUY)) + RestClient.PRINT.replace("{order_id}", orderId);
		return new ResponseEntity<>(new Response<Order>(new Order(url), true), HttpStatus.OK);
	}

	@GetMapping(RestClient.PRINT)
	public ResponseEntity<byte[]> print(
			@PathVariable(name = "order_id") String orderId) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = orderId + ".pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    return new ResponseEntity<>(client.print(orderId), headers, HttpStatus.OK);
	}

	@PostMapping(RestClient.CANCEL)
	public ResponseEntity<Response<Order>> cancel(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		return client.cancel(login, password, locale, orderId, "cancel", "cancel");
	}

	@PostMapping(RestClient.INFO)
	public ResponseEntity<Response<Order>> info(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(name = "with_fees", required = false) String withFees) {
		return client.info(login, password, locale, orderId, withFees);
	}

	@PostMapping(RestClient.ANNULMENT)
	public ResponseEntity<Response<Order>> annulment(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(required = false) String description) {
		//return client.annulment(login, password, locale, orderId, description);
		return client.cancel(login, password, locale, orderId, "annulment", "annulment");
	}

	@PostMapping(RestClient.AUTO_RETURN)
	public ResponseEntity<Response<?>> autoReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(required = false) String description) {
		return client.autoReturn(login, password, locale, orderId, null, description);
	}

	@PostMapping(RestClient.RETURN)
	public ResponseEntity<Response<Order>> manualReturn(HttpServletRequest request) {
		//return client.manualReturn(request);
		throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
	}

	@PostMapping(RestClient.TICKET_AUTO_RETURN)
	public ResponseEntity<Response<?>> ticketAutoReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		//return client.ticketAutoReturn(login, password, locale, ticketId, description);
		return client.autoReturn(login, password, locale, null, ticketId, description);
	}

	@PostMapping(RestClient.TICKET_AUTO_RETURN_PRICE)
	public ResponseEntity<Response<Ticket>> ticketAutoReturnPrice(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		//return client.ticketAutoReturnPrice(login, password, locale, ticketId, description);
		throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
	}

	@PostMapping(RestClient.TICKET_ANNULMENT)
	public ResponseEntity<Response<Ticket>> ticketAnnulment(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		//return client.ticketAnnulment(login, password, locale, ticketId, description);
		throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
	}

	@PostMapping(RestClient.TICKET_RETURN)
	public ResponseEntity<Response<Ticket>> ticketManualReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String amount) {
		//return client.ticketManualReturn(login, password, locale, ticketId, description, amount);
		throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
	}

}
