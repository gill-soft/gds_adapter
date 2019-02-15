package com.gillsoft;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.concurrent.PoolType;
import com.gillsoft.concurrent.ThreadPoolStore;
import com.gillsoft.matrix.model.Response;

public class RequestSender<T> {
	
	private static final Logger LOGGER = LogManager.getLogger(RequestSender.class);
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			T container, ContainerDataFiller<T> filler) {
		return getDataResponse(method, httpMethod, requestParams, type, poolType, container, filler, Config.getConnections(), null);
	}
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			T container, ContainerDataFiller<T> filler, Connection connection, CacheDataReader<T> cacheReader) {
		return getDataResponse(method, httpMethod, requestParams, type, poolType, container, filler,
				connection != null ? Collections.singletonList(connection) : Config.getConnections(), cacheReader);
	}
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			Connection connection) {
		return getDataResponse(method, httpMethod, requestParams, type, poolType, null, null, Collections.singletonList(connection), null);
	}
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			Connection connection, CacheDataReader<T> cacheReader) {
		return getDataResponse(method, httpMethod, requestParams, type, poolType, null, null, Collections.singletonList(connection), cacheReader);
	}
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			T container, ContainerDataFiller<T> filler, List<Connection> connections, CacheDataReader<T> cacheReader) {
		List<Callable<ResponseEntity<Response<T>>>> callables = new ArrayList<>();
		for (Connection connection : connections) {
			if (connection.isAvailable()) {
				callables.add(() -> {
					ResponseEntity<Response<T>> responseEntity = null;
					if (cacheReader != null) {
						Response<T> response = cacheReader.read(connection);
						if (response != null) {
							response.setFromCache(true);
							responseEntity = new ResponseEntity<Response<T>>(response, HttpStatus.valueOf(response.getStatusCode()));
						}
					}
					if (responseEntity == null) {
						URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + method)
								.queryParams(requestParams)
								.build().toUri();
						try {
							RequestEntity<Object> request = new RequestEntity<>(httpMethod, uri);
							responseEntity = connection.getTemplate().exchange(request, type);
						} catch (RestClientException e) {
							LOGGER.error(e.getMessage());
							return null;
						}
					}
					if (responseEntity == null) {
						return null;
					}
					responseEntity.getBody().setConnection(connection);
					return responseEntity;
				});
			}
		}
		List<ResponseEntity<Response<T>>> results = ThreadPoolStore.getResult(poolType, callables);
		ResponseEntity<Response<T>> result = null;
		if (filler != null) {
			Response<T> response = null;
			for (ResponseEntity<Response<T>> responseEntity : results) {
				if (responseEntity != null
						&& (responseEntity.getStatusCode() == HttpStatus.ACCEPTED
								|| responseEntity.getStatusCode() == HttpStatus.OK)) {
					filler.fill(responseEntity, container);
					if (result == null) {
						response = new Response<>();
						response.setStatus(true);
						response.setData(container);
						response.setStatusCode(responseEntity.getStatusCodeValue());
						result = new ResponseEntity<Response<T>>(response, responseEntity.getStatusCode());
					}
				}
			}
		}
		if (result == null) {
			for (ResponseEntity<Response<T>> responseEntity : results) {
				if (responseEntity != null) {
					if (responseEntity.getBody() != null) {
						responseEntity.getBody().setStatusCode(responseEntity.getStatusCodeValue());
					}
					result = new ResponseEntity<Response<T>>(responseEntity.getBody(), responseEntity.getStatusCode());
					break;
				}
			}
		}
		return result;
	}
	
	public ResponseEntity<T> getResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<T> type, PoolType poolType,
			T container, ContainerFiller<T> filler, Connection connection, CacheReader<T> cacheReader) {
		return getResponse(method, httpMethod, requestParams, type, poolType, container, filler,
				connection != null ? Collections.singletonList(connection) : Config.getConnections(), cacheReader);
	}
	
	public ResponseEntity<T> getResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<T> type, PoolType poolType,
			T container, ContainerFiller<T> filler, List<Connection> connections, CacheReader<T> cacheReader) {
		List<Callable<ResponseEntity<T>>> callables = new ArrayList<>();
		for (Connection connection : connections) {
			if (connection.isAvailable()) {
				callables.add(() -> {
					if (cacheReader != null) {
						T response = cacheReader.read(connection);
						if (response != null) {
							return new ResponseEntity<T>(response, HttpStatus.OK);
						}
					}
					URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + method)
							.queryParams(requestParams)
							.build().toUri();
					try {
						RequestEntity<Object> request = new RequestEntity<>(httpMethod, uri);
						ResponseEntity<T> response = connection.getTemplate().exchange(request, type);
						return response;
					} catch (RestClientException e) {
						LOGGER.error(e.getMessage());
						return null;
					}
				});
			}
		}
		List<ResponseEntity<T>> results = ThreadPoolStore.getResult(poolType, callables);
		ResponseEntity<T> result = null;
		if (filler != null) {
			for (ResponseEntity<T> responseEntity : results) {
				if (responseEntity != null
						&& (responseEntity.getStatusCode() == HttpStatus.ACCEPTED
								|| responseEntity.getStatusCode() == HttpStatus.OK)) {
					filler.fill(responseEntity, container);
					if (result == null) {
						result = new ResponseEntity<T>(container, responseEntity.getStatusCode());
					}
				}
			}
		}
		if (result == null) {
			for (ResponseEntity<T> responseEntity : results) {
				if (responseEntity != null) {
					result = new ResponseEntity<T>(responseEntity.getBody(), responseEntity.getStatusCode());
					break;
				}
			}
		}
		return result;
	}
	
	public interface ContainerDataFiller<T> {
		
		public void fill(ResponseEntity<Response<T>> response, T container);
		
	}
	
	public interface ContainerFiller<T> {
		
		public void fill(ResponseEntity<T> response, T container);
		
	}
	
	public interface CacheDataReader<T> {
		
		public Response<T> read(Connection connection);
		
	}
	
	public interface CacheReader<T> {
		
		public T read(Connection connection);
		
	}
	
}
