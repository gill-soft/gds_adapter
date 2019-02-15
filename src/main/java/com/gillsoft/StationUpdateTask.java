package com.gillsoft;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.Station;
import com.gillsoft.util.ContextProvider;

public class StationUpdateTask extends CountriesUpdateTask {

	private static final long serialVersionUID = 6171704745377891606L;

	public StationUpdateTask(Connection connection, MultiValueMap<String, String> params) {
		super(connection, params);
	}
	
	@Override
	public void run() {
		/*RestClient client = ContextProvider.getBean(RestClient.class);
		ResponseEntity<Response<Set<Station>>> response = client.getStations(params, false, Config.getConnection(connection.getId()));
		if (response != null
				&& (response.getStatusCode() == HttpStatus.ACCEPTED
					|| response.getStatusCode() == HttpStatus.OK)) {
			writeObjectIgnoreAge(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.STATIONS_CACHE_KEY, connection.getId(), params),
					response.getBody(), Config.getCacheStationsUpdateDelay());
		} else {
			writeObject(client.getRedisCache(),
					RestClient.getCacheKey(RestClient.STATIONS_CACHE_KEY, connection.getId(), params),
					null, Config.getCacheErrorTimeToLive(), Config.getCacheErrorUpdateDelay());
		}*/
	}

}
