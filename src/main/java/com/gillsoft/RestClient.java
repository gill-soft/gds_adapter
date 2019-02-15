package com.gillsoft;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.cache.CacheHandler;
import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.concurrent.PoolType;
import com.gillsoft.concurrent.ThreadPoolStore;
import com.gillsoft.logging.SimpleRequestResponseLoggingInterceptor;
import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.matrix.model.Discount;
import com.gillsoft.matrix.model.Geo;
import com.gillsoft.matrix.model.Locale;
import com.gillsoft.matrix.model.Order;
import com.gillsoft.matrix.model.Parameters;
import com.gillsoft.matrix.model.PathPoint;
import com.gillsoft.matrix.model.Point;
import com.gillsoft.matrix.model.Price;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.ReturnRule;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.matrix.model.Seat;
import com.gillsoft.matrix.model.Station;
import com.gillsoft.matrix.model.Ticket;
import com.gillsoft.matrix.model.Trip;
import com.gillsoft.model.Currency;
import com.gillsoft.model.Customer;
import com.gillsoft.model.Document;
import com.gillsoft.model.IdentificationDocumentType;
import com.gillsoft.model.Lang;
import com.gillsoft.model.Locality;
import com.gillsoft.model.RequiredField;
import com.gillsoft.model.RestError;
import com.gillsoft.model.Route;
import com.gillsoft.model.RoutePoint;
import com.gillsoft.model.RouteType;
import com.gillsoft.model.SeatStatus;
import com.gillsoft.model.SeatsScheme;
import com.gillsoft.model.Segment;
import com.gillsoft.model.ServiceItem;
import com.gillsoft.model.Tariff;
import com.gillsoft.model.request.OrderRequest;
import com.gillsoft.model.request.TripSearchRequest;
import com.gillsoft.model.response.OrderResponse;
import com.gillsoft.model.response.TripSearchResponse;
import com.gillsoft.util.RestTemplateUtil;
import com.gillsoft.util.StringUtil;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestClient {

	private static Logger LOGGER = LogManager.getLogger(RestClient.class);

	public static final String COUNTRIES_CACHE_KEY = "gds.countries.";
	public static final String CITIES_CACHE_KEY = "gds.cities.";
	public static final String STATIONS_CACHE_KEY = "gds.stations.";
	public static final String ROUTE_CACHE_KEY = "matrix.route.";
	public static final String TRIPS_CACHE_KEY = "matrix.trips";

	public static final String PING = "/get/ping";
	public static final String LOCALES = "/get/locales";
	public static final String CURRENCIES = "/get/currency-list";
	public static final String COUNTRIES = "/get/countries";
	public static final String CITIES = "/get/cities";
	public static final String STATIONS = "/geo/station";
	public static final String TRIPS = "/get/trips";
	public static final String RULES = "/get/trip/return-rules";
	public static final String ROUTE = "/get/route-info";
	public static final String SEATS_MAP = "/get/seatsMap";
	public static final String FREE_SEATS = "/get/freeSeats";
	public static final String NEW_ORDER = "/order/new";
	public static final String RESERVE = "/order/reserve";
	public static final String BUY = "/order/buy";
	public static final String PRINT = "/order/print/{order_id}";
	public static final String INFO = "/order/info";
	public static final String CANCEL = "/order/cancel";
	public static final String ANNULMENT = "/order/annulment";
	public static final String AUTO_RETURN = "/order/auto-return";
	public static final String RETURN = "/order/return";
	public static final String TICKET_AUTO_RETURN = "/ticket/auto-return";
	public static final String TICKET_AUTO_RETURN_PRICE = "/ticket/auto-return-price";
	public static final String TICKET_ANNULMENT = "/ticket/annulment";
	public static final String TICKET_RETURN = "/ticket/return";

	public static final String MAP = "/GEO/map/195/%s";
	
	private static final ParameterizedTypeReference<List<Locality>> localityTypeReference = new ParameterizedTypeReference<List<Locality>>() { };
	private static final ParameterizedTypeReference<TripSearchResponse> tripSearchResponseTypeReference = new ParameterizedTypeReference<TripSearchResponse>() { };
	private static final ParameterizedTypeReference<List<RequiredField>> requiredFieldTypeReference = new ParameterizedTypeReference<List<RequiredField>>() { };
	private static final ParameterizedTypeReference<OrderResponse> orderResponseTypeReference = new ParameterizedTypeReference<OrderResponse>() { };
	private static final ParameterizedTypeReference<List<Tariff>> tariffTypeReference = new ParameterizedTypeReference<List<Tariff>>() { };
	private static final ParameterizedTypeReference<List<com.gillsoft.model.mapper.Map>> mapperTypeReference = new ParameterizedTypeReference<List<com.gillsoft.model.mapper.Map>>() { };
	
	private static final String FIELD_LOCALE = "locale";
	private static final String FIELD_INTERVAL_ID = "interval_id";
	private static final String FIELD_LOGIN = "login";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_ORDER_ID = "order_id";
	private static final String FIELD_CURRENCY = "currency";

	private static final String MSG_ORDER_NOT_FOUND = "Order not found [order_id=%s]";
	
	private static final Map<String, Long> map = new HashMap<>();
	
	private static final String LOCALITY_TYPE_CITY = "6";

	@Autowired
	@Qualifier("MemoryCacheHandler")
	private CacheHandler memoryCache;

	@Autowired
	@Qualifier("RedisMemoryCache")
	private CacheHandler redisCache;

	private RestTemplate template;
	// для запросов поиска с меньшим таймаутом
	private RestTemplate searchTemplate;
	// маппинг
	private RestTemplate mapperTemplate;


	//private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

	/*@Scheduled(initialDelay = 5000, fixedDelay = 300000)
	public ResponseEntity<Response<Object>> ping() {
		return ping(Config.getLogin(), Config.getPassword(), true);
	}*/
	
	/*private <T> T getResult(RestTemplate template, Request request, String method, HttpMethod httpMethod,
			ParameterizedTypeReference<T> type) throws ResponseError {
		URI uri = UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri();
		RequestEntity<Request> requestEntity = new RequestEntity<Request>(request,
				(httpMethod.equals(HttpMethod.POST) ? postHeaders : headers), httpMethod, uri);
		ResponseEntity<T> response = template.exchange(requestEntity, type);
		return response.getBody();
	}
	
	private <T> RequestEntity<T> getRequestEntity(T request, HttpMethod httpMethod, String method) {
		return new RequestEntity<T>(request, (httpMethod.equals(HttpMethod.POST) ? postHeaders : headers), httpMethod,
				UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri());
	}*/

	private <T> RequestEntity<T> getRequestEntity(T request, HttpMethod httpMethod, String method, String login,
			String password) {
		HttpHeaders headers = null;
		if (login != null && password != null) {
			headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + Base64.encodeBase64String((login + ':' + password).getBytes()));
		}
		return new RequestEntity<>(request, headers, httpMethod,
				UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri());
	}

	private <T> RequestEntity<T> getRequestEntityMapper(T request, HttpMethod httpMethod, String method, String login,
			String password) {
		return new RequestEntity<>(request, null, httpMethod,
				UriComponentsBuilder.fromUriString(Config.getUrlMapper() + method).build().toUri());
	}

	//TODO @Scheduled(initialDelay = 5000, fixedDelay = 300000)
	public void updateGeo() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic "
	            + Base64.encodeBase64String((Config.getLogin() + ":" + Config.getPassword()).getBytes()));
		RequestEntity<List<Locality>> requestEntity = new RequestEntity<>(null, headers,
				RestClientMethod.CITIES.getHttpMethod(), getUri(RestClientMethod.CITIES.getGdsUrl()));
		ResponseEntity<List<Locality>> templateResponse = template.exchange(requestEntity, localityTypeReference);
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null && !templateResponse.getBody().isEmpty()) {
			try {
				memoryCache.write(templateResponse.getBody().stream().filter(f -> f.getParent() == null)
						.collect(Collectors.toList()), getCacheParams(RestClient.COUNTRIES_CACHE_KEY, null, true));
				memoryCache.write(templateResponse.getBody().stream()
						.filter(f -> templateResponse.getBody().stream()
								.filter(c -> c.getParent() != null && f.getId().equals(c.getParent().getId())).count() == 0)
						.collect(Collectors.toList()), getCacheParams(RestClient.STATIONS_CACHE_KEY, null, true));
//				cache.write(templateResponse.getBody().stream().filter(f -> Integer.valueOf(f.getId()) < 100).collect(Collectors.toList()), getGeoParams(RestClient.COUNTRIES_CACHE_KEY));
				// TODO
				//Object o = cache.read(getCacheParams(RestClient.COUNTRIES_CACHE_KEY, null, true));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			//templateResponse.getBody().stream().filter(f -> f.getParent() == null).collect(Collectors.toList());
		}
	}

	private Map<String, Object> getCacheParams(String key, Long timeToLive, Boolean ignoreAge) {
		Map<String, Object> cacheParams = new HashMap<>();
		cacheParams.put(RedisMemoryCache.OBJECT_NAME, key);
		if (timeToLive != null && timeToLive != 0L) {
			cacheParams.put(RedisMemoryCache.TIME_TO_LIVE, timeToLive);
		}
		cacheParams.put(RedisMemoryCache.IGNORE_AGE, ignoreAge);
		return cacheParams;
	}

	public RestClient() {
		template = createNewPoolingTemplate(Config.getUrl(), Config.getRequestTimeout());
		searchTemplate = createNewPoolingTemplate(Config.getUrl(), Config.getSearchRequestTimeout());
		mapperTemplate = createNewPoolingTemplate(Config.getUrlMapper(), Config.getSearchRequestTimeout());
	}

	private RestTemplate createNewPoolingTemplate(String url, int requestTimeout) {
		RestTemplate template = new RestTemplate(
				new BufferingClientHttpRequestFactory(RestTemplateUtil.createPoolingFactory(url, 300, requestTimeout)));
		template.setInterceptors(Collections.singletonList(new SimpleRequestResponseLoggingInterceptor()));
		template.setErrorHandler(new RestTemplateResponseErrorHandler());
		return template;
	}

/*	public ResponseEntity<Response<Object>> ping(String login, String password) {
		return ping(login, password, false);
	}

	private ResponseEntity<Response<Object>> ping(String login, String password, boolean checkConnection) {
		List<Callable<ResponseEntity<Response<Object>>>> callables = new ArrayList<>();
		for (Connection connection : Config.getConnections()) {
			callables.add(() -> {
				URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + PING)
						.queryParam(FIELD_LOGIN, login)
						.queryParam(FIELD_PASSWORD, password)
						.build().toUri();
				try {
					RequestEntity<Object> request = new RequestEntity<>(HttpMethod.GET, uri);
					ResponseEntity<Response<Object>> response = connection.getTemplate().exchange(request,
							new ParameterizedTypeReference<Response<Object>>() {});
					if (checkConnection) {
						connection.setAvailable(response.getBody().isStatus());
					}
					return response;
				} catch (RestClientException e) {
					if (checkConnection) {
						connection.setAvailable(false);
					}
					return null;
				}
			});
		}
		// возвращаем первый удачный пинг
		List<ResponseEntity<Response<Object>>> pings = ThreadPoolStore.getResult(PoolType.RESOURCE_INFO, callables);
		for (ResponseEntity<Response<Object>> ping : pings) {
			if (ping != null
					&& ping.getBody().isStatus()) {
				return ping;
			}
		}
		// возвращаем первый неудачный пинг
		for (ResponseEntity<Response<Object>> ping : pings) {
			if (ping != null) {
				return ping;
			}
		}
		return null;
	}*/

	public ResponseEntity<Response<Map<String, Locale>>> getLocales(String login, String password, String locale) {
		Map<String, Locale> locales = new HashMap<>();
		Stream.of(Lang.values()).forEach(lang -> {
			String langValue = String.valueOf(lang);
			locales.put(langValue, new Locale(langValue, langValue));
		});
		return new ResponseEntity<>(new Response<>(locales, true), HttpStatus.OK);
	}

	public ResponseEntity<Response<Map<String, String>>> getCurrencies(String login, String password, String locale) {
		Map<String, String> currencies = new HashMap<>();
		Stream.of(Currency.values()).forEach(lang -> {
			String currencyValue = String.valueOf(lang);
			currencies.put(currencyValue, currencyValue);
		});
		return new ResponseEntity<>(new Response<>(currencies, true), HttpStatus.OK);
	}

	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale, boolean useCache) {
		//return getCountries(createLoginParams(login, password, locale), useCache, null);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(FIELD_LOGIN, login);
		params.add(FIELD_PASSWORD, password);
		List<Locality> localityList = getAllLocalities(params);
		if (localityList != null && !localityList.isEmpty()) {
			Set<Country> countries = new HashSet<>();
			Lang lang = getLocaleLang(locale);
			localityList.stream().filter(locality -> locality.getParent() == null).forEach(locality -> {
				Country country = new Country();
				country.setId(Integer.parseInt(locality.getId()));
				country.setLocale(locale == null ? Lang.EN.toString().toLowerCase() : locale);
				country.setName(locality.getName(lang));
				countries.add(country);
			});
			return new ResponseEntity<>(countries, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<Set<Country>> getCountries(MultiValueMap<String, String> params, boolean useCache, Connection connection) {
		try {
			List<Locality> countriesCache = (List<Locality>) memoryCache.read(getCacheParams(RestClient.COUNTRIES_CACHE_KEY, null, true));
			if (countriesCache != null) {
				Set<Country> countries = new HashSet<>();
				Lang lang = getLocaleLang(params);
				MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
				countriesCache.forEach(l -> {
					l.setName(Lang.EN, l.getName(Lang.EN));
					l.setName(Lang.RU, l.getName(Lang.RU));
					countries.add(mapLocality(l, Country.class, lang, mapperFactory));
				});
				return new ResponseEntity<>(countries, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		//TODO getCountries 
		return new ResponseEntity<>(null, HttpStatus.CONFLICT);
	}

	private <T> T mapLocality(Locality locality, Class<T> clazz, Lang lang, MapperFactory mapperFactory) {
		mapperFactory.classMap(Locality.class, clazz).field("id", "id")
				.field(String.format("name[\"%s\"]", lang), "name").register();
		MapperFacade mapper = mapperFactory.getMapperFacade();
		return mapper.map(locality, clazz);
	}
	
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale, String countryId, boolean useCache) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		return getCities(params, countryId);
	}
	
	private URI getUri(String url) {
		return UriComponentsBuilder.fromUriString(Config.getUrl() + url).build().toUri();
	}

	private List<Locality> getAllLocalities(MultiValueMap<String, String> params) {
		ResponseEntity<List<Locality>> templateResponse = template
				.exchange(
						getRequestEntity(null, RestClientMethod.CITIES.getHttpMethod(), RestClientMethod.CITIES.getGdsUrl(),
								getLogin(params), getPassword(params)),
						localityTypeReference);
		if (templateResponse != null && templateResponse.hasBody()) {
			return templateResponse.getBody();
		}
		return new ArrayList<>();
	}
	
	public ResponseEntity<Response<Set<City>>> getCities(MultiValueMap<String, String> params, String countryId) {
		List<Locality> localityList = getAllLocalities(params);
		if (localityList != null && !localityList.isEmpty()) {
			Set<City> cities = new HashSet<>();
			Lang localeLang = getLocaleLang(params);
			localityList.stream().filter(locality -> LOCALITY_TYPE_CITY.equals(locality.getType()))
					.forEach(locality -> {
						City city = new City();
						city.setId(Integer.valueOf(locality.getId()));
						if (locality.getLatitude() != null) {
							city.setLatitude(locality.getLatitude().toString());
						}
						if (locality.getLongitude() != null) {
							city.setLongitude(locality.getLongitude().toString());
						}
						city.setName(locality.getName(localeLang));
						if (locality.getParent() != null) {
							city.setGeoCountryId(getCountryId(localityList, locality.getParent().getId()));
							// city.setGeoRegionId(Integer.valueOf(locality.getParent().getId()));
						}
						if (countryId == null || (countryId != null && city.getGeoCountryId() != null
								&& countryId.equals(String.valueOf(city.getGeoCountryId())))) {
							cities.add(city);
						}
					});
			return new ResponseEntity<>(new Response<>(cities, true), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	/**
	 * Страна для НП/остановки
	 * @param localityList
	 * @param parentId
	 * @return
	 */
	private Integer getCountryId(List<Locality> localityList, String parentId) {
		if (parentId == null) {
			return null;
		}
		Iterator<Locality> localityIterator = localityList.stream().filter(f -> f.getId().equals(parentId))
				.iterator();
		if (localityIterator.hasNext()) {
			Locality parent = localityIterator.next();
			if (parent.getParent() == null) {
				return Integer.parseInt(parent.getId());
			} else {
				return getCountryId(localityList, parent.getParent().getId());
			}
		}
		return null;
	}
	
	private Lang getLocaleLang(String locale) {
		if (locale == null) {
			return Lang.EN;
		}
		Lang lang = Lang.EN;
		try {
			lang = Lang.valueOf(locale.toUpperCase());
		} catch (Exception e) { }
		return lang;
	}
	
	private Lang getLocaleLang(MultiValueMap<String, String> params) {
		if (params == null || !params.containsKey(FIELD_LOCALE)) {
			return getLocaleLang((String)null);
		}
		return getLocaleLang(params.getFirst(FIELD_LOCALE));
	}
	
	public ResponseEntity<Response<List<Trip>>> getTrips(HttpServletRequest request, boolean useCache) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Enumeration<String> requestParams = request.getParameterNames();
		while (requestParams.hasMoreElements()) {
			String name = requestParams.nextElement();
			params.add(name, request.getParameter(name));
		}
		return getTrips(params, useCache, null);
	}
	
	public ResponseEntity<Response<List<Trip>>> getTrips(MultiValueMap<String, String> params, boolean useCache, Connection connection) {
		TripSearchRequest request = new TripSearchRequest();
		request.setLocalityPairs(
				Arrays.asList(new String[][] { { getGdsPointIdByMatrixPointId(params.getFirst("depart_locality")),
						getGdsPointIdByMatrixPointId(params.getFirst("arrive_locality")) } }));
		try {
			request.setDates(Arrays.asList(StringUtil.dateFormat.parse(params.getFirst("depart_date"))));
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		if (params.containsKey(FIELD_CURRENCY)) {
			try {
				request.setCurrency(com.gillsoft.model.Currency.valueOf(params.getFirst(FIELD_CURRENCY)));
			} catch (Exception e) { }
		}
		String login = getLogin(params);
		String password = getPassword(params);
		ResponseEntity<TripSearchResponse> templateResponse = template.exchange(
				getRequestEntity(request, RestClientMethod.TRIPS_INIT.getHttpMethod(), RestClientMethod.TRIPS_INIT.getGdsUrl(),
						login, password),
				tripSearchResponseTypeReference);
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			templateResponse = getTrips(templateResponse.getBody().getSearchId(), login, password);
			if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
				ArrayList<Trip> tripList = new ArrayList<>();
				Lang lang = getLocaleLang(params);
				final Map<String, Locality> localities = templateResponse.getBody().getLocalities();
				templateResponse.getBody().getSegments().forEach(
						(id, segment) -> tripList.add(createTrip(id, segment, lang, localities, login, password)));
				return new ResponseEntity<>(new Response<>(tripList, true), HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(new Response<>(new ArrayList<>(), true), HttpStatus.OK);
	}
	
	private Trip createTrip(String id, Segment segment, Lang lang, Map<String, Locality> localities, String login,
			String password) {
		Trip trip = new Trip();
		trip.setTripId(id);
		trip.setIntervalId(id);
		trip.setRouteId(segment.getRoute().getId());
		trip.setRouteCode(segment.getRoute().getName(lang));
		trip.setDepartDate(DateUtils.truncate(segment.getDepartureDate(), Calendar.DATE));
		trip.setDepartTime(StringUtil.timeFormat.format(segment.getDepartureDate()));
		trip.setArriveDate(DateUtils.truncate(segment.getArrivalDate(), Calendar.DATE));
		trip.setArriveTime(StringUtil.timeFormat.format(segment.getArrivalDate()));
		trip.setDepartStationId(segment.getDeparture().getId());
		trip.setDepartStation(getLocalityName(localities, trip.getDepartStationId(), lang));
		trip.setArriveStationId(segment.getArrival().getId());
		trip.setArriveStation(getLocalityName(localities, trip.getArriveStationId(), lang));
		trip.setDepartCityId(getParentId(localities, trip.getDepartStationId()));
		trip.setDepartCity(getLocalityName(localities, trip.getDepartCityId(), lang));
		trip.setArriveCityId(getParentId(localities, trip.getArriveStationId()));
		trip.setArriveCity(getLocalityName(localities, trip.getArriveCityId(), lang));
		trip.setTimeInWay(segment.getTimeInWay());
		trip.setTariff(segment.getPrice().getAmount());
		trip.setPrice(new Price(segment.getPrice().getAmount(), segment.getPrice().getAmount()));
		trip.setCurrency(String.valueOf(segment.getPrice().getCurrency()));
		trip.setExchangeRate(1f);
		trip.setDocFields(getDocFields(trip.getTripId(), login, password));
		if (segment.getRoute() != null && RouteType.INTERNATIONAL.equals(segment.getRoute().getType())) {
			trip.setInternational(true);
		}
		trip.setDiscounts(getTripTariffs(trip.getTripId(), login, password, lang));
		return trip;
	}
 
	@SuppressWarnings("unchecked")
	private Map<String, Boolean> getDocFields(String tripId, String login, String password) {
		Map<String, Object> cacheParams = null;
		try {
			// TODO
			String tripIdString = StringUtil.fromBase64AsString(tripId);
			tripIdString = tripIdString.substring(tripIdString.indexOf("\"resourceId\":"));
			String resourceId = tripIdString.substring(13, tripIdString.indexOf(','));
			/*TripIdModel trip = new TripIdModel().create(tripId);
			cacheParams = getCacheParams(String.valueOf(trip.getResourceId()), 900000L, false);*/
			cacheParams = getCacheParams("doc_fields_" + resourceId, 900000L, false);
			Map<String, Boolean> map = (Map<String, Boolean>) memoryCache.read(cacheParams);
			if (map != null) {
				return map;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ResponseEntity<List<RequiredField>> templateResponse = template.exchange(
				getRequestEntity(null, RestClientMethod.REQUIRED_FIELDS.getHttpMethod(),
						String.format(RestClientMethod.REQUIRED_FIELDS.getGdsUrl(), tripId), login, password),
				requiredFieldTypeReference);
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			Map<String, Boolean> map = templateResponse.getBody().stream()
					.collect(Collectors.toMap(key -> mapDocField(key), value -> Boolean.TRUE));
			try {
				memoryCache.write(map, cacheParams);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return map;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Discount> getTripTariffs(String tripId, String login, String password, Lang lang) {
		Map<String, Object> cacheParams = null;
		try {
			cacheParams = getCacheParams("trip_tariffs_" + tripId, 900000L, false);
			List<Discount> list = (List<Discount>) memoryCache.read(cacheParams);
			if (list != null) {
				return list;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ResponseEntity<List<Tariff>> templateResponse = template.exchange(
				getRequestEntity(null, RestClientMethod.TARIFFS.getHttpMethod(),
						String.format(RestClientMethod.TARIFFS.getGdsUrl(), tripId, lang), login, password),
				tariffTypeReference);
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			List<Discount> list = new ArrayList<>();
			templateResponse.getBody().stream().forEach(tariff -> {
				Discount discount = new Discount();
				discount.setId(tariff.getId());
				discount.setDiscountType(tariff.getCode());
				discount.setAmount(tariff.getValue());
				if (tariff.getDescription() != null && !tariff.getDescription().isEmpty()) {
					discount.setI18n(tariff.getDescription().entrySet().stream()
							.collect(Collectors.toMap(key -> String.valueOf(key.getKey()),
									value -> new Parameters(null, null, value.getValue()))));
				}
				list.add(discount);
			});
			try {
				memoryCache.write(list, cacheParams);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			return list;
		}
		return null;
	}
	
	private Discount getTripTariff(String tripId, String tariffId, String login, String password, Lang lang) {
		List<Discount> discountList = getTripTariffs(tripId, login, password, lang);
		if (discountList != null) {
			Iterator<Discount> discountIterator = discountList.stream().filter(discount -> discount.getId().equals(tariffId)).iterator();
			if (discountIterator.hasNext()) {
				return discountIterator.next();
			}
		}
		return null;
	}
	
	private String mapDocField(RequiredField requiredFiled) {
		switch (requiredFiled) {
		case BIRTHDAY:
			return "birth_date";
		case DOCUMENT_TYPE:
			return "doc_type";
		case DOCUMENT_NUMBER:
			return "doc_number";
		case EMAIL:
			return "email";
		case NAME:
			return "name";
		case PHONE:
			return "phone";
		case SEAT:
			return "seat";
		case SURNAME:
			return "surname";
		case TARIFF:
			return "tariff";
		default:
			return requiredFiled.toString().toLowerCase();
		}
	}
	
	private ResponseEntity<TripSearchResponse> getTrips(String searchId, String login, String password) {
		ResponseEntity<TripSearchResponse> templateResponse = template.exchange(
				getRequestEntity(null, RestClientMethod.TRIPS.getHttpMethod(),
						String.format(RestClientMethod.TRIPS.getGdsUrl(), searchId), login, password),
				tripSearchResponseTypeReference);
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null
				&& templateResponse.getBody().getSearchId() != null) {
			return getTrips(templateResponse.getBody().getSearchId(), login, password);
		}
		return templateResponse;
	}
	
	private String getLocalityName(Map<String, Locality> localities, String localityId, Lang lang) {
		if (localities == null || localities.isEmpty() || localityId == null || localityId.isEmpty() || lang == null) {
			return null;
		}
		Locality locality = localities.get(localityId);
		if (locality != null) {
			return locality.getName(lang);
		}
		return null;
	}
	
	private String getParentId(Map<String, Locality> localities, String localityId) {
		if (localities == null || localities.isEmpty() || localityId == null || localityId.isEmpty()) {
			return null;
		}
		Locality locality = localities.get(localityId);
		if (locality != null && locality.getParent() != null) {
			return locality.getParent().getId();
		}
		return null;
	}
	
	public ResponseEntity<Response<List<ReturnRule>>> getReturnRules(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add(FIELD_INTERVAL_ID, trimConnectionId(FIELD_INTERVAL_ID, intervalId));
		return new RequestSender<List<ReturnRule>>().getDataResponse(RULES, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<ReturnRule>>>() {}, PoolType.SEARCH,
				Config.getConnection(intervalId));
	}
	
	public ResponseEntity<Response<RouteInfo>> getRoute(String login, String password, String locale, String routeId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		return getRoute(params, routeId);
	}
	
	public ResponseEntity<Response<RouteInfo>> getRoute(MultiValueMap<String, String> params, String routeId) {
		Lang lang = getLocaleLang(params);
		ResponseEntity<Route> templateResponse = template.exchange(getRequestEntity(null, RestClientMethod.ROUTE.getHttpMethod(),
				String.format(RestClientMethod.ROUTE.getGdsUrl(), routeId, lang), getLogin(params),
				getPassword(params)), new ParameterizedTypeReference<Route>() {
				});
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			RouteInfo routeInfo = new RouteInfo();
			routeInfo.setRoute(new com.gillsoft.matrix.model.Route());
			routeInfo.getRoute().setCode(templateResponse.getBody().getName(lang));
			if (templateResponse.getBody().getPath() != null) {
				routeInfo.getRoute().setPath(new ArrayList<>());
				templateResponse.getBody().getPath().stream().forEach(routePoint -> {
					if (routePoint.getLocality() != null) {
						routeInfo.getRoute().getPath().add(createPathPoint(routePoint, lang));
					}
				});
			}
			return new ResponseEntity<>(new Response<>(routeInfo, true), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	private PathPoint createPathPoint(RoutePoint routePoint, Lang lang) {
		PathPoint pathPoint = new PathPoint();
		pathPoint.setDepartTime(routePoint.getDepartureTime());
		Geo geo = new Geo();
		geo.setPoint(new Point());
		geo.getPoint().setId(Integer.valueOf(routePoint.getLocality().getId()));
		if (routePoint.getLocality().getLatitude() != null) {
			geo.getPoint().setLatitude(routePoint.getLocality().getLatitude().toString());
		}
		if (routePoint.getLocality().getLongitude() != null) {
			geo.getPoint().setLongitude(routePoint.getLocality().getLongitude().toString());
		}
		geo.getPoint().setName(routePoint.getLocality().getName(lang));
		geo.getPoint().setNativeAddress(routePoint.getLocality().getAddress(lang));
		if (routePoint.getLocality().getParent() != null) {
			geo.setLocality(new com.gillsoft.matrix.model.Locality());
			geo.getLocality().setId(Integer.valueOf(routePoint.getLocality().getParent().getId()));
		}
		pathPoint.setGeo(geo);
		return pathPoint;
	}
	
	public Response<List<List<Seat>>> getSeatsMap(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		ResponseEntity<SeatsScheme> templateResponse = null;
		try {
			templateResponse = template.exchange(
					getRequestEntity(null, RestClientMethod.SEATS_MAP.getHttpMethod(),
							String.format(RestClientMethod.SEATS_MAP.getGdsUrl(), intervalId),
							getLogin(params), getPassword(params)),
					new ParameterizedTypeReference<SeatsScheme>() {
					});
		} catch (Exception e) {
			RestError restError = RestTemplateResponseErrorHandler.getRestError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
			return new Response<>(null, false, restError.getName());
		}
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			List<List<Seat>> seats = new ArrayList<>();
			templateResponse.getBody().getScheme().forEach((key,
					value) -> seats.addAll(value.stream()
							.map(mapper -> mapper.stream().map(seat -> new Seat(seat)).collect(Collectors.toList()))
							.collect(Collectors.toList())));
			return new Response<>(seats, true);
		}
		return new Response<>();
	}
	
	public Response<Map<String, String>> getFreeSeats(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		ResponseEntity<List<com.gillsoft.model.Seat>> templateResponse = null;
		try {
			templateResponse = template.exchange(
					getRequestEntity(null, RestClientMethod.FREE_SEATS.getHttpMethod(),
							String.format(RestClientMethod.FREE_SEATS.getGdsUrl(), intervalId),
							getLogin(params), getPassword(params)),
					new ParameterizedTypeReference<List<com.gillsoft.model.Seat>>() {
					});
		} catch (Exception e) {
			RestError restError = RestTemplateResponseErrorHandler.getRestError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
			return new Response<>(null, false, restError.getName());
		}
		if (templateResponse != null && templateResponse.hasBody() && templateResponse.getBody() != null) {
			Map<String, String> freeSeats = new HashMap<>();
			templateResponse.getBody().stream().filter(freeSeat -> SeatStatus.FREE.equals(freeSeat.getStatus()))
					.forEach(seat -> freeSeats.put(seat.getId(), seat.getNumber()));
			return new Response<>(freeSeats, true);
		}
		return new Response<>();
	}
	
	public ResponseEntity<Response<Order>> create(HttpServletRequest request) {
		MultiValueMap<String, String> params = getRequestParamsMap(request);
		int maxCustomerNo = Integer.parseInt(params.getFirst("maxCustomerNo"));
		if (maxCustomerNo < 0) {
			throw new RestClientException("No customers found");
		}
		//ResponseEntity<Response<Order>> response = null;
		String login = getLogin(params);
		String password = getPassword(params);
		ResponseEntity<Response<Order>> response = orderOperation(getOrderRequest(params, maxCustomerNo), login,
				password, getLocale(params), null, RestClientMethod.NEW_ORDER, "new", "new ticket");
		try {
			Map<String, String> map = new HashMap<>();
			map.put(FIELD_LOGIN, login);
			map.put(FIELD_PASSWORD, password);
			map.put(FIELD_LOCALE, params.getFirst(FIELD_LOCALE));
//			map.put(FIELD_ORDER_ID, response.getBody().getData().getNumber());
//			// добавляем все билеты по связке hash->id
//			map.putAll(response.getBody().getData().getTickets().values().stream()
//					.flatMap(ticketList -> ticketList.stream())
//					.collect(Collectors.toMap(Ticket::getHash, Ticket::getNumber)));
			redisCache.write(map, getCacheParams("order_" + response.getBody().getData().getHash(), null, true));
		} catch (Exception e) {
			LOGGER.info("Order create redis cache write error for OrderId=" + response.getBody().getData().getNumber() + ", uuid=" + response.getBody().getData().getHash());
			LOGGER.error(e.getMessage(), e);
		}
		/*ResponseEntity<OrderResponse> templateResponse = template.exchange(getRequestEntity(getOrderRequest(params, maxCustomerNo),
				RestClientMethod.NEW_ORDER.getHttpMethod(), RestClientMethod.NEW_ORDER.getGdsUrl(), login, password),
				orderResponseTypeReference);
		if (templateResponse != null && templateResponse.hasBody()) {
			checkOrderResponse(templateResponse.getBody());
			Order order = getOrder(templateResponse.getBody(), getLocaleLang(params), "new", "new ticket", getOrderUUID(params));
			try {
				Map<String, String> map = new HashMap<>();
				map.put(FIELD_LOGIN, login);
				map.put(FIELD_PASSWORD, password);
				map.put(FIELD_LOCALE, params.getFirst(FIELD_LOCALE));
				map.put(FIELD_ORDER_ID, templateResponse.getBody().getOrderId());
				redisCache.write(map, getCacheParams("order_" + order.getHash(), null, true));
			} catch (Exception e) {
				LOGGER.info("Order create redis cache write error for OrderId=" + templateResponse.getBody().getOrderId() + ", uuid=" + order.getHash());
				LOGGER.error(e.getMessage(), e);
			}
			response = new ResponseEntity<>(new Response<Order>(order), HttpStatus.OK);
		}*/
		return response;
	}

	private MultiValueMap<String, String> getRequestParamsMap(HttpServletRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Enumeration<String> requestParams = request.getParameterNames();
		int maxCustomerNo = -1;
		while (requestParams.hasMoreElements()) {
			String name = requestParams.nextElement();
			params.add(name, request.getParameter(name));
			if (name.contains("name")) {
				int customerNo = Integer.parseInt(name.replaceAll("([a-zA-Z]|\\[|\\])", ""));
				if (customerNo > maxCustomerNo) {
					maxCustomerNo = customerNo;
				}
			}
		}
		params.add("maxCustomerNo", String.valueOf(maxCustomerNo));
		return params;
	}

	private OrderRequest getOrderRequest(MultiValueMap<String, String> params, int maxCustomerNo) {
		String orderEmail = params.getFirst("email");
		String orderPhone = params.getFirst("phone");
		String intervalId0 = params.getFirst(FIELD_INTERVAL_ID + "[0]");
		String intervalId1 = params.getFirst(FIELD_INTERVAL_ID + "[1]");
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setCustomers(new HashMap<>());
		orderRequest.setServices(new ArrayList<>());
		for (int i = 0; i <= maxCustomerNo; i++) {
			String index = "[" + i + "]";
			String tripNo = "[0]";
			// new customer
			Customer customer = new Customer(String.valueOf(i));
			customer.setName(params.getFirst("name" + index));
			customer.setSurname(params.getFirst("surname" + index));
			customer.setEmail(orderEmail);
			customer.setPhone(orderPhone);
			try {
				String birthday = params.getFirst("birth_date" + index);
				if (birthday != null && !birthday.isEmpty()) {
					customer.setBirthday(StringUtil.dateFormat.parse(birthday));
				}
			} catch (Exception e) { }
			customer.setDocumentNumber(params.getFirst("doc_number" + index));
			String docType = params.getFirst("doc_type" + index);
			if (docType != null && !docType.isEmpty()) {
				try {
					customer.setDocumentType(IdentificationDocumentType.valueOf(docType));
				} catch (Exception e) {
					throw new RestClientException("Unknown document type [doc_type=" + docType + ']');
				}
			}
			orderRequest.getCustomers().put(String.valueOf(i), customer);
			// new service
			String tripNoIndex = tripNo + index;
			ServiceItem serviceItem = new ServiceItem();
			serviceItem.setCustomer(new Customer(String.valueOf(i)));
			if (params.containsKey("seat" + tripNoIndex)) {
				com.gillsoft.model.Seat seat = new com.gillsoft.model.Seat();
				seat.setId(params.getFirst("seat" + tripNoIndex));
				serviceItem.setSeat(seat);
			}
			if (params.containsKey("discount" + tripNoIndex)) {
				com.gillsoft.model.Price price = new com.gillsoft.model.Price();
				price.setTariff(new com.gillsoft.model.Tariff());
				price.getTariff().setId(params.getFirst("discount" + tripNoIndex));
				serviceItem.setPrice(price);
			}
			orderRequest.getServices().add(serviceItem);
			if (params.containsKey(FIELD_CURRENCY)) {
				orderRequest.setCurrency(com.gillsoft.model.Currency.valueOf(params.getFirst(FIELD_CURRENCY)));
			}
			// service segment
			serviceItem.setSegment(new Segment(intervalId0));
		}
		return orderRequest;
	}

	/**
	 * Проверка на ошибки ответа на запрос OrderRequest 
	 * @param com.gillsoft.model.response.OrderResponse
	 */
	private void checkOrderResponse(OrderResponse orderResponse) {
		if (orderResponse.getError() != null) {
			throw new RestClientException(orderResponse.getError().getMessage());
		} else if (orderResponse.getServices() != null) {
			String CONFIRM_ERROR = "CONFIRM_ERROR";
			orderResponse.getServices().add(orderResponse.getServices().get(0));
			if (orderResponse.getServices().size() == 1 && (orderResponse.getServices().get(0).getError() != null
					|| CONFIRM_ERROR.equals(orderResponse.getServices().get(0).getStatus()))) {
				throw new RestClientException("service id:" + orderResponse.getServices().get(0).getId() + " number:"
						+ orderResponse.getServices().get(0).getNumber() + " error:"
						+ (orderResponse.getServices().get(0).getError() != null
								? orderResponse.getServices().get(0).getError().getMessage() : CONFIRM_ERROR));
			} else if (orderResponse.getServices().stream()
					.filter(service -> service.getError() == null && !CONFIRM_ERROR.equals(service.getStatus()))
					.count() == 0) {
				throw new RestClientException(orderResponse.getServices().stream()
						.filter(service -> (service.getError() != null && service.getError().getMessage() != null)
								|| CONFIRM_ERROR.equals(service.getStatus()))
						.map(service -> "service id:" + service.getId() + " number:"
								+ service.getNumber() + " error:"
								+ ((service.getError() != null && service.getError().getMessage() != null)
										? service.getError().getMessage() : CONFIRM_ERROR))
						.collect(Collectors.joining(",")));
			}
		}
	}

	/**
	 * Преобразование заказа в формат заказа Matrix'а
	 * @param com.gillsoft.model.response.OrderResponse
	 * @return com.gillsoft.matrix.model.Order
	 */
	private Order getOrder(OrderResponse orderResponse, String orderStatus, String ticketStatus, Lang lang) {
		Order order = new Order();
		order.setNumber(orderResponse.getOrderId());
		order.setStatus(orderStatus);
		setOrderPnoneEmail(order, orderResponse);
		order.setHash(getHash(orderResponse.getOrderId(), null));
		orderResponse.getServices().forEach(service -> {
			// добавляем билет в заказ
			if (order.getTickets() == null) {
				order.setTickets(new HashMap<>());
			}
			List<Ticket> ticketList = order.getTickets().get(service.getSegment().getId());
			if (ticketList == null) {
				ticketList = new ArrayList<>();
				order.getTickets().put(service.getSegment().getId(), ticketList);
			}
			ticketList.add(getTicket(service, orderResponse, ticketStatus, lang));
		});
		order.setDocuments(orderResponse.getDocuments());
		return order;
	}

	private Ticket getTicket(ServiceItem service, OrderResponse orderResponse, String ticketStatus, Lang lang) {
		Ticket ticket = new Ticket();
		ticket.setNumber(service.getNumber() != null ? service.getNumber() : service.getId());
		ticket.setTripId(service.getSegment().getId());
		ticket.setNumber(service.getNumber());
		ticket.setHash(getHash(orderResponse.getOrderId(), ticket.getNumber()));
		Customer customer = orderResponse.getCustomers().get(service.getCustomer().getId());
		if (customer != null) {
			ticket.setPassName(customer.getName());
			ticket.setPassSurname(customer.getSurname());
		}
		ticket.setStatus(ticketStatus);
		if (service.getPrice() != null && service.getPrice().getTariff() != null) {
			ticket.setCost(service.getPrice().getTariff().getValue());
			ticket.setPrice(service.getPrice().getAmount());
		}
		if (service.getSeat() != null) {
			ticket.setSeat(service.getSeat().getId());
			ticket.setSeatNumber(service.getSeat().getNumber());
		}
		Segment serviceSegment = orderResponse.getSegments().get(service.getSegment().getId());
		if (serviceSegment != null) {
			// перевозчик
			if (serviceSegment.getCarrier() != null && serviceSegment.getCarrier().getId() != null) {
				ticket.setCarrierCode(serviceSegment.getCarrier().getId());
				ticket.setCarrierName(orderResponse.getOrganisations().get(ticket.getCarrierCode()).getName(lang));
			}
			ticket.setRouteCode(serviceSegment.getNumber());
			ticket.setDepartAt(serviceSegment.getDepartureDate());
			ticket.setArriveAt(serviceSegment.getArrivalDate());
			ticket.setGeoPointFrom(serviceSegment.getDeparture().getId());
			ticket.setGeoPointTo(serviceSegment.getArrival().getId());
			// условия возврата
			if (serviceSegment.getPrice() != null && serviceSegment.getPrice().getTariff() != null
					&& serviceSegment.getPrice().getTariff().getReturnConditions() != null
					&& !serviceSegment.getPrice().getTariff().getReturnConditions().isEmpty()) {
				ticket.setReturnRules(
						new ArrayList<>(serviceSegment.getPrice().getTariff().getReturnConditions().size()));
				serviceSegment.getPrice().getTariff().getReturnConditions().forEach(returnCondition -> {
					ReturnRule returnRule = new ReturnRule();
					returnRule.setTitle(returnCondition.getTitle(lang));
					returnRule.setDescription(returnCondition.getDescription(lang));
					if (returnCondition.getMinutesBeforeDepart() != null) {
						returnRule.setMinutesBeforeDepart(returnCondition.getMinutesBeforeDepart());
						returnRule.setActiveTo(DateUtils.addMinutes(serviceSegment.getArrivalDate(), returnCondition.getMinutesBeforeDepart()));
					}
					ticket.getReturnRules().add(returnRule);
				});
			}
		}
		return ticket;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getOrderParams(String orderId) {
		try {
			return (HashMap<String, String>) redisCache.read(getCacheParams("order_" + orderId, null, true));
		} catch (Exception e) {
			LOGGER.warn("getOrderUUID.redisCache.read error for order_id=" + orderId);
			throw new RestClientException(e.getMessage());
		}
	}

	private void setOrderPnoneEmail(Order order, OrderResponse body) {
		Iterator<Customer> iterator = body.getCustomers().values().stream().filter(customer -> customer.getPhone() != null && customer.getEmail() != null).iterator();
		if (iterator.hasNext()) {
			Customer customer = iterator.next();
			order.setEmail(customer.getEmail());
			order.setPhone(customer.getPhone());
		} else {
			iterator = body.getCustomers().values().stream().filter(customer -> customer.getEmail() != null).iterator();
			if (iterator.hasNext()) {
				order.setEmail(iterator.next().getEmail());
			}
			iterator = body.getCustomers().values().stream().filter(customer -> customer.getPhone() != null).iterator();
			if (iterator.hasNext()) {
				order.setEmail(iterator.next().getPhone());
			}
		}
	}

	public ResponseEntity<Response<Order>> reserve(String login, String password, String locale, String orderId) {
		return orderOperation(login, password, locale, orderId, RestClientMethod.RESERVE, "booking", "reservation");
	}

	public ResponseEntity<Response<Order>> buy(String login, String password, String locale, String orderId) {
		return orderOperation(login, password, locale, orderId, RestClientMethod.BUY, null, null);
	}

	public ResponseEntity<Response<Order>> cancel(String login, String password, String locale, String orderId, String orderStatus, String ticketStatus) {
		return orderOperation(login, password, locale, orderId, RestClientMethod.CANCEL, orderStatus, ticketStatus);
	}

	public ResponseEntity<Response<Order>> info(String login, String password, String locale, String orderId, String withFees) {
		return orderOperation(login, password, locale, orderId, RestClientMethod.INFO, null, null);
	}

	public ResponseEntity<Response<Order>> annulment(String login, String password, String locale, String orderId, String description) {
		return orderOperation(ANNULMENT, login, password, locale, orderId, null, description);
	}

	public ResponseEntity<Response<?>> autoReturn(String login, String password, String locale, String orderId, String ticketId, String description) {
		//return orderOperation(AUTO_RETURN, login, password, locale, orderId, null, description);
		ResponseEntity<OrderResponse> orderResponse = orderOperation(null, login, password,
				orderId == null ? ticketId : orderId, RestClientMethod.INFO);
		if (orderResponse != null && orderResponse.getBody() != null) {
			checkOrderResponse(orderResponse.getBody());
			// если возврат конкретного билета - оставляем только его
			if (ticketId != null) {
				Iterator<ServiceItem> servicesIterator = orderResponse.getBody().getServices().iterator();
				String ticketNumber = getHashTicketId(orderId);
				while (servicesIterator.hasNext()) {
					if (!ticketNumber.equals(servicesIterator.next().getNumber())) {
						servicesIterator.remove();
					}
				}
				// если нечего возвращать (не нашли билет в заказе по номеру) - выходим
				if (orderResponse.getBody().getServices().isEmpty()) {
					String errorMsg = "Order/ticket not found " + (orderId == null ? ticketId : orderId);
					LOGGER.warn("autoReturn: " + errorMsg);
					throw new RestClientException(errorMsg);
				}
			}
			OrderRequest orderRequest = new OrderRequest();
			orderRequest.setOrderId(orderResponse.getBody().getOrderId());
			orderRequest.setServices(orderResponse.getBody().getServices());
			orderRequest.setCustomers(orderResponse.getBody().getCustomers());
			orderResponse = orderOperation(orderRequest, login, password, orderId, RestClientMethod.RETURN_PREPARE);
			if (orderResponse != null && orderResponse.getBody() != null) {
				checkOrderResponse(orderResponse.getBody());
				orderResponse = orderOperation(orderRequest, login, password, orderId, RestClientMethod.RETURN_CONFIRM);
				if (orderResponse != null && orderResponse.getBody() != null) {
					if (ticketId != null) {
						return new ResponseEntity<>(
								new Response<Ticket>(getTicket(orderResponse.getBody().getServices().get(0),
										orderResponse.getBody(), null, getLocaleLang(locale))),
								HttpStatus.OK);
					} else {
						return new ResponseEntity<>(
								new Response<Order>(
										getOrder(orderResponse.getBody(), null, null, getLocaleLang(locale))),
								HttpStatus.OK);
					}
				} else {
					String errorMsg = "Order/ticket return error " + (orderId == null ? ticketId : orderId);
					LOGGER.warn("autoReturn:" + errorMsg);
					throw new RestClientException(errorMsg);
				}
			} else {
				String errorMsg = "Order/ticket return prepare error " + (orderId == null ? ticketId : orderId);
				LOGGER.warn("autoReturn:" + errorMsg);
				throw new RestClientException(errorMsg);
			}
		}
		throw new RestClientException("Order/ticket not found " + (orderId == null ? ticketId : orderId));
	}

	public ResponseEntity<Response<Order>> manualReturn(HttpServletRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Enumeration<String> paramNames = request.getParameterNames();
		String orderId = null;
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if (Objects.equals(FIELD_ORDER_ID, name)) {
				orderId = request.getParameter(name);
				params.add(name, trimConnectionId(name, orderId));
			} else if (name.contains("amount")) {
				params.add(trimConnectionId(name, name, 4) + "]", request.getParameter(name));
			} else {
				params.add(name, request.getParameter(name));
			}
		}
		Connection connection = Config.getConnection(orderId);
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(RETURN, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}

	/*private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale, String orderId) {
		return orderOperation(method, login, password, locale, orderId, null, null, null);
	}*/

	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale, String orderId, String withFees, String description) {
		return orderOperation(method, login, password, locale, orderId, withFees, description, null);
	}

	private ResponseEntity<Response<Order>> orderOperation(String method, String login,
			String password, String locale, String orderId, String withFees, String description, String amount) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add(FIELD_ORDER_ID, trimConnectionId(FIELD_ORDER_ID, orderId));
		params.add("with_fees", withFees);
		params.add("description", description);
		params.add("amount", amount);
		Connection connection = Config.getConnection(orderId);
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(method, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}

	private <T> ResponseEntity<OrderResponse> orderOperation(T request, String login, String password, String orderId,
			RestClientMethod restClientMethod) {
		try {
			return template.exchange(getRequestEntity(request, restClientMethod.getHttpMethod(),
					restClientMethod.equals(RestClientMethod.NEW_ORDER) ? restClientMethod.getGdsUrl()
							: String.format(restClientMethod.getGdsUrl(), getHashOrderId(orderId)),
					login, password), orderResponseTypeReference);
		} catch (Exception e) {
			RestError restError = RestTemplateResponseErrorHandler
					.getRestError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
			throw new RestClientException(restError.getName());
		}
	}

	private <T> ResponseEntity<Response<Order>> orderOperation(T request, String login, String password, String locale,
			String orderId, RestClientMethod restClientMethod, String orderStatus, String ticketStatus) {
		ResponseEntity<OrderResponse> templateResponse = orderOperation(request, login, password, orderId,
				restClientMethod);
		if (templateResponse != null && templateResponse.hasBody()) {
			checkOrderResponse(templateResponse.getBody());
			return new ResponseEntity<>(
					new Response<Order>(
							getOrder(templateResponse.getBody(), orderStatus, ticketStatus, getLocaleLang(locale))),
					HttpStatus.OK);
		}
		String msg = String.format(MSG_ORDER_NOT_FOUND, orderId);
		LOGGER.warn(msg);
		throw new RestClientException(msg);
	}

	private ResponseEntity<Response<Order>> orderOperation(String login, String password, String locale, String orderId,
			RestClientMethod restClientMethod, String orderStatus, String ticketStatus) {
		return orderOperation(null, login, password, locale, orderId, restClientMethod, orderStatus, ticketStatus);
	}

	public ResponseEntity<Response<Ticket>> ticketAutoReturn(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_AUTO_RETURN, login, password, locale, ticketId, description, null);
	}

	public ResponseEntity<Response<Ticket>> ticketAutoReturnPrice(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_AUTO_RETURN_PRICE, login, password, locale, ticketId, description, null);
	}

	public ResponseEntity<Response<Ticket>> ticketAnnulment(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_ANNULMENT, login, password, locale, ticketId, description, null);
	}

	public ResponseEntity<Response<Ticket>> ticketManualReturn(String login, String password, String locale, String ticketId, String description, String amount) {
		return ticketOperation(TICKET_RETURN, login, password, locale, ticketId, description, amount);
	}

	private ResponseEntity<Response<Ticket>> ticketOperation(String method, String login, String password,
			String locale, String ticketId, String description, String amount) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("ticket_id", trimConnectionId("ticket_id", ticketId));
		params.add("description", description);
		params.add("amount", amount);
		Connection connection = Config.getConnection(ticketId);
		ResponseEntity<Response<Ticket>> response = new RequestSender<Ticket>().getDataResponse(method, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Ticket>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			response.getBody().getData().setHash(addConnectionId(response.getBody().getData().getHash(), connection));
		}
		return response;
	}

	public <T> boolean checkResponse(ResponseEntity<Response<T>> response) {
		return (response.getStatusCode() == HttpStatus.ACCEPTED
				|| response.getStatusCode() == HttpStatus.OK)
				&& response.getBody().getData() != null;
	}

	private void updateOrder(Order order, Connection connection) {
		if (order.getHash() != null) {
			order.setHash(addConnectionId(order.getHash(), connection));
		}
		if (order.getTickets() != null) {
			Map<String, List<Ticket>> tickets = new HashMap<>(order.getTickets().size());
			for (Entry<String, List<Ticket>> ticketList : order.getTickets().entrySet()) {
				tickets.put(addConnectionId(ticketList.getKey(), connection), ticketList.getValue());
				for (Ticket ticket : ticketList.getValue()) {
					ticket.setHash(addConnectionId(ticket.getHash(), connection));
				}
			}
			order.setTickets(tickets);
		}
	}

	private MultiValueMap<String, String> createLoginParams(String login, String password, String locale) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(FIELD_LOGIN, login);
		params.add(FIELD_PASSWORD, password);
		params.add(FIELD_LOCALE, locale);
		return params;
	}

	private String getLoginParam(MultiValueMap<String, String> loginParams, String paramName) {
		if (loginParams.containsKey(paramName)) {
			return String.valueOf(loginParams.getFirst(paramName));
		}
		return "";
	}

	private String getLogin(MultiValueMap<String, String> loginParams) {
		return getLoginParam(loginParams, FIELD_LOGIN);
	}

	private String getPassword(MultiValueMap<String, String> loginParams) {
		return getLoginParam(loginParams, FIELD_PASSWORD);
	}

	private String getLocale(MultiValueMap<String, String> loginParams) {
		return getLoginParam(loginParams, FIELD_LOCALE);
	}

	private String addConnectionId(String id, Connection connection) {
		if (Config.getConnections().size() == 1) {
			return id;
		}
		return id + String.format("%03d", connection.getId());
	}

	private String trimConnectionId(String name, String id) {
		return trimConnectionId(name, id, 3);
	}

	private String trimConnectionId(String name, String id, int charsCount) {
		if (Config.getConnections().size() == 1) {
			return id;
		}
		if (id == null
				|| id.isEmpty()
				|| id.length() < charsCount) {
			throw new RestClientException("Invalid parameter " + name);
		}
		return id.substring(0, id.length() - charsCount);
	}

	public CacheHandler getMemoryCache() {
		return memoryCache;
	}

	public CacheHandler getRedisCache() {
		return redisCache;
	}

	public static String getCacheKey(String key, int connectionId, MultiValueMap<String, String> params) {
		List<String> values = new ArrayList<>();
		for (List<String> list : params.values()) {
			values.addAll(list.stream().filter(Objects::nonNull).collect(Collectors.toList()));
		}
		Collections.sort(values);
		values.add(0, String.valueOf(connectionId));
		values.add(0, key);
		return String.join(".", values);
	}

	public static Object readCacheObject(CacheHandler cache, String cacheKey, Runnable updateTask, int requestTimeout) {
		int tryCount = 0;
		Map<String, Object> cacheParams = new HashMap<>();
		cacheParams.put(RedisMemoryCache.OBJECT_NAME, cacheKey);
		cacheParams.put(RedisMemoryCache.UPDATE_TASK, updateTask);
		do {
			try {
				return cache.read(cacheParams);
			} catch (IOCacheException e) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException ie) {
				}
			}
		} while (tryCount++ < requestTimeout / 1000);
		return null;
	}

	public byte[] print(String orderId) {
		Map<String, String> orderParams = getOrderParams(orderId);
		if (orderParams != null) {
			ResponseEntity<Response<Order>> responseEntity = orderOperation(orderParams.get(FIELD_LOGIN),
					orderParams.get(FIELD_PASSWORD), orderParams.get(FIELD_LOCALE), orderId, RestClientMethod.PRINT, null,
					null);
			if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
				List<Document> documentList = responseEntity.getBody().getData().getDocuments();
				if (documentList == null || documentList.isEmpty()) {
					return new byte[0];
				}
				// возвращаем документ если один или объединяем несколько в один в противном случае
				if (documentList.size() == 1) {
					return StringUtil.fromBase64(documentList.get(0).getBase64());
				} else {
					PDFMergerUtility ut = new PDFMergerUtility();
					documentList.forEach(document -> ut.addSource(
							new ByteArrayInputStream(StringUtil.fromBase64(documentList.get(0).getBase64()))));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ut.setDestinationStream(baos);
					try {
						ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
						return baos.toByteArray();
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				throw new RestClientException("Generate ticket error [order_id=" + orderId + ']');
			} else {
				throw new RestClientException(String.format(MSG_ORDER_NOT_FOUND, orderId));
			}
		} else {
			throw new RestClientException("Order params not found [order_id=" + orderId + ']');
		}
	}

	private String getHash(String orderId, String ticketId) {
		//e652dbea-e37c-4c7d-ad75-d601e44a24ee
		return String.format("%08X", Long.parseLong(orderId)) + "-0000-0000-0000-"
				+ (ticketId == null ? "000000000000" : String.format("%012X", Long.parseLong(ticketId)));
	}

	private String getHashOrderId(String hash) {
		if (!isHash(hash)) {
			return hash;
		}
		return String.valueOf(Integer.parseInt(hash.substring(0, 8), 16));
	}

	private String getHashTicketId(String hash) {
		if (!isHash(hash)) {
			return hash;
		}
		return String.valueOf(Integer.parseInt(hash.substring(hash.length() - 12), 16));
	}

	private boolean isHash(String hash) {
		return hash.toUpperCase().replaceAll("^[\\dA-F]{8}(-{0,1}[\\dA-F]{4}){3}-{0,1}[\\dA-F]{12}$", "").isEmpty();
	}

	private String getGdsPointIdByMatrixPointId(String matrixPointId) {
		Long gdsId = map.get(matrixPointId);
		if (gdsId == null) {
			ResponseEntity<List<com.gillsoft.model.mapper.Map>> mapResponse = mapperTemplate.exchange(getRequestEntityMapper(null, HttpMethod.GET,
					String.format(MAP,  matrixPointId), null, null), mapperTypeReference);
			if (mapResponse != null && mapResponse.hasBody() && !mapResponse.getBody().isEmpty()) {
				map.put(matrixPointId, mapResponse.getBody().get(0).getId());
				return String.valueOf(mapResponse.getBody().get(0).getId());
			}
		}
		return matrixPointId;
	}

	public static void main(String[] args) {
		/*List<RequiredField> l = new ArrayList<>();
		l.add(RequiredField.BIRTHDAY);
		l.add(RequiredField.CITIZENSHIP);
		Object s = l.stream().collect(Collectors.toMap(RequiredField::name, t -> Boolean.TRUE));
		if (s != null)
			System.out.println();*/
		//-------------------------------------------
		/*List<List<Seat>> seats = new ArrayList<>();
		List<List<String>> strings = new ArrayList<>();
		strings.add(Arrays.asList(new String[] { "1" }));
		strings.add(Arrays.asList(new String[] { "2", "2" }));
		strings.add(Arrays.asList(new String[] { "3", "3", "3" }));
		List<List<Seat>> o = strings.stream().map(mapper -> mapper.stream().map(seat -> new Seat(seat)).collect(Collectors.toList())).collect(Collectors.toList());
		if (o != null) {
			seats.addAll(o);
			System.out.println(o.toString());
		}*/
		System.out.println(new HashSet<String>(Arrays.asList(new String[] {"1", "2", "3"})));
	}

}