package com.gillsoft.matrix.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Station extends Country {

	private static final long serialVersionUID = 6911054255089858670L;
	
	private String address;
	
	@JsonProperty("city_id")
	private int cityId;
	
	@JsonProperty("city_name")
	private String cityName;
	
	@JsonProperty("country_id")
	private int countryId;
	
	@JsonProperty("country_name")
	private String countryName;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Station)) {
			return false;
		}
		Station station = (Station) obj;
		return super.equals(obj)
				&& cityId == station.getCityId()
				&& countryId == station.getCountryId()
				&& Objects.equals(cityName, station.getCityName())
				&& Objects.equals(countryName, station.getCountryName())
				&& Objects.equals(address, station.getAddress());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 31 + Objects.hash(address, cityId, cityName, cityId, countryName);
	}

}
