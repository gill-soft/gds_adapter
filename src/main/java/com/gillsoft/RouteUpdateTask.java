package com.gillsoft;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.gillsoft.cache.AbstractUpdateTask;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.util.ContextProvider;

public class RouteUpdateTask extends AbstractUpdateTask {

	private static final long serialVersionUID = -6859368060540072639L;
	
	private Connection connection;
	
	protected MultiValueMap<String, String> params;
	
	public RouteUpdateTask(Connection connection, MultiValueMap<String, String> params) {
		this.connection = connection;
		this.params = params;
	}

	@Override
	public void run() {
//		RestClient client = ContextProvider.getBean(RestClient.class);
//		ResponseEntity<Response<RouteInfo>> response = client.getRoute(params, false, Config.getConnection(connection.getId()));
//		long timeToLive = 0;
//		long updateDelay = 0;
//		if (response != null 
//				&& (response.getStatusCode() == HttpStatus.ACCEPTED
//					|| response.getStatusCode() == HttpStatus.OK)) {
//			timeToLive = getTimeToLive(response.getBody().getData());
//			updateDelay = Config.getCacheTripUpdateDelay();
//		} else {
//			timeToLive = Config.getCacheErrorTimeToLive();
//			updateDelay = Config.getCacheErrorUpdateDelay();
//		}
//		writeObject(client.getCache(), RestClient.getCacheKey(RestClient.ROUTE_CACHE_KEY, connection.getId(), params), response.getBody(), timeToLive, updateDelay);
	}
	
	// время жизни до конца существования маршрута
	private long getTimeToLive(RouteInfo routeInfo) {
		if (Config.getCacheTripTimeToLive() != 0) {
			return Config.getCacheTripTimeToLive();
		}
		if (routeInfo.getRoute().getEnded() == null
				|| routeInfo.getRoute().getEnded().getTime() < System.currentTimeMillis()) {
			return Config.getCacheErrorTimeToLive();
		}
		return routeInfo.getRoute().getEnded().getTime() - System.currentTimeMillis();
	}

}
