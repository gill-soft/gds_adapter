package com.gillsoft;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.util.ContextProvider;

public class CitiesUpdateTask extends CountriesUpdateTask {

	private static final long serialVersionUID = 1792318881958028832L;

	public CitiesUpdateTask(Connection connection, MultiValueMap<String, String> params) {
		super(connection, params);
	}
	
	@Override
	public void run() {
		/*RestClient client = ContextProvider.getBean(RestClient.class);
		ResponseEntity<Response<Set<City>>> response = client.getCities(params, false, Config.getConnection(connection.getId()));
		if (response != null
				&& (response.getStatusCode() == HttpStatus.ACCEPTED
					|| response.getStatusCode() == HttpStatus.OK)) {
			writeObjectIgnoreAge(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.CITIES_CACHE_KEY, connection.getId(), params),
					response.getBody(), Config.getCacheStationsUpdateDelay());
		} else {
			writeObject(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.CITIES_CACHE_KEY, connection.getId(), params),
					response.getBody(), Config.getCacheErrorTimeToLive(), Config.getCacheErrorUpdateDelay());
		}*/
	}

}
