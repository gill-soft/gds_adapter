package com.gillsoft;

import java.io.Serializable;
import java.util.Collections;
import java.util.Properties;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.gillsoft.logging.SimpleRequestResponseLoggingInterceptor;
import com.gillsoft.util.RestTemplateUtil;

public class Connection implements Serializable {
	
	private static final long serialVersionUID = 4983141817430304358L;

	private int id;
	
	private boolean available;

	private String url;
	
	transient private RestTemplate template;
	
	// для запросов поиска с меньшим таймаутом
	transient private RestTemplate searchTemplate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RestTemplate getTemplate() {
		return template;
	}

	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	public RestTemplate getSearchTemplate() {
		return searchTemplate;
	}

	public void setSearchTemplate(RestTemplate searchTemplate) {
		this.searchTemplate = searchTemplate;
	}

	public void fillProperties(Properties properties, int number) {
		id = number;
		url = properties.getProperty("url." + number);
		
		template = createNewPoolingTemplate(url, Integer.valueOf(properties.getProperty("request.timeout")));
		searchTemplate = createNewPoolingTemplate(url, Integer.valueOf(properties.getProperty("request.search.timeout")));
	}

	public RestTemplate createNewPoolingTemplate(String url, int requestTimeout) {
		RestTemplate template = new RestTemplate(new BufferingClientHttpRequestFactory(
				RestTemplateUtil.createPoolingFactory(url, 300, requestTimeout)));
		template.setInterceptors(Collections.singletonList(
				new SimpleRequestResponseLoggingInterceptor()));
		template.setErrorHandler(new RestTemplateResponseErrorHandler());
		return template;
	}
	
}
