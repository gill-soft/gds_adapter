package com.gillsoft.matrix.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class City extends Country {

	private static final long serialVersionUID = -443963281701008310L;

	@JsonProperty("geo_country_id")
	private Integer geoCountryId;

	@JsonProperty("geo_region_id")
	private Integer geoRegionId;
	private String latitude;
	private String longitude;

	public Integer getGeoCountryId() {
		return geoCountryId;
	}

	public void setGeoCountryId(Integer geoCountryId) {
		this.geoCountryId = geoCountryId;
	}

	public Integer getGeoRegionId() {
		return geoRegionId;
	}

	public void setGeoRegionId(Integer geoRegionId) {
		this.geoRegionId = geoRegionId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof City)) {
			return false;
		}
		City city = (City) obj;
		return super.equals(obj)
				&& geoCountryId == city.getGeoCountryId()
				&& geoRegionId == city.getGeoRegionId()
				&& latitude == city.getLatitude()
				&& longitude == city.getLongitude();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 31 + Objects.hash(geoCountryId, geoRegionId, latitude, longitude);
	}

}
