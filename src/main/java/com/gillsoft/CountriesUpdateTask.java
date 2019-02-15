package com.gillsoft;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.gillsoft.cache.AbstractUpdateTask;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.util.ContextProvider;

public class CountriesUpdateTask extends AbstractUpdateTask {

	private static final long serialVersionUID = 8873082805767541195L;

	protected Connection connection;

	protected MultiValueMap<String, String> params;

	public CountriesUpdateTask(Connection connection, MultiValueMap<String, String> params) {
		this.connection = connection;
		this.params = params;
	}

	@Override
	public void run() {
		RestClient client = ContextProvider.getBean(RestClient.class);
		ResponseEntity<Set<Country>> response = client.getCountries(params, false, Config.getConnection(connection.getId()));
		if (response != null
				&& (response.getStatusCode() == HttpStatus.ACCEPTED
					|| response.getStatusCode() == HttpStatus.OK)) {
			writeObjectIgnoreAge(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.COUNTRIES_CACHE_KEY, connection.getId(), params),
					response.getBody(), Config.getCacheStationsUpdateDelay());
		} else {
			writeObject(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.COUNTRIES_CACHE_KEY, connection.getId(), params),
					null, Config.getCacheErrorTimeToLive(), Config.getCacheErrorUpdateDelay());
		}
	}

}
