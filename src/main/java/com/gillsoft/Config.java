package com.gillsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.client.RestClientException;

public class Config {

	private static Properties properties;
	
	private static List<Connection> connections = new ArrayList<>();

	static {
		try {
			Resource resource = new ClassPathResource("resource.properties");
			properties = PropertiesLoaderUtils.loadProperties(resource);
			
			// загружаем настройки серверов
			/*int number = 0;
			while (properties.getProperty("url." + number) != null) {
				Connection connection = new Connection();
				connection.fillProperties(properties, number++);
				connections.add(connection);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Connection> getConnections() {
		return connections;
	}
	
	public static Connection getConnection(int id) {
		for (Connection connection : connections) {
			if (connection.getId() == id) {
				return connection;
			}
		}
		throw new RestClientException("Invalid one of sended parameter");
	}
	
	public static Connection getConnection(String id) {
		if (connections.size() == 1) {
			return connections.get(0);
		}
		// берем последние 3 цифры с конца
		id = id.substring(id.length() - 3, id.length());
		try {
			return getConnection(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			throw new RestClientException("Invalid one of sended parameter");
		}
	}

	public static String getUrl() {
		return properties.getProperty("url.gds");
	}
	
	public static String getUrlMapper() {
		return properties.getProperty("url.mapper");
	}

	public static String getLogin() {
		return properties.getProperty("login");
	}

	public static String getPassword() {
		return properties.getProperty("password");
	}

	public static int getRequestTimeout() {
		return Integer.valueOf(properties.getProperty("request.timeout"));
	}

	public static int getSearchRequestTimeout() {
		return Integer.valueOf(properties.getProperty("request.search.timeout"));
	}

	public static long getCacheTripTimeToLive() {
		return Long.valueOf(properties.getProperty("cache.trip.time.to.live"));
	}

	public static long getCacheTripUpdateDelay() {
		return Long.valueOf(properties.getProperty("cache.trip.update.delay"));
	}

	public static long getCacheErrorTimeToLive() {
		return Long.valueOf(properties.getProperty("cache.error.time.to.live"));
	}

	public static long getCacheErrorUpdateDelay() {
		return Long.valueOf(properties.getProperty("cache.error.update.delay"));
	}

	public static long getCacheStationsUpdateDelay() {
		return Long.valueOf(properties.getProperty("cache.stations.update.delay"));
	}

	public static long getCacheRouteTimeToLive() {
		return Long.valueOf(properties.getProperty("cache.route.time.to.live"));
	}

	public static long getCacheRouteUpdateDelay() {
		return Long.valueOf(properties.getProperty("cache.route.update.delay"));
	}

}
