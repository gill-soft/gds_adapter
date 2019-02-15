package com.gillsoft.matrix.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class PathPoint implements Serializable {

	private static final long serialVersionUID = -2635028067938790195L;

	@JsonProperty("station_code")
	private String stationCode;

	@JsonProperty("point_type")
	private String pointType;

	@JsonProperty("depart_day")
	private String departDay;

	@JsonProperty("depart_time")
	private String departTime;

	@JsonProperty("stopping_time")
	private String stoppingTime;

	@JsonProperty("arrive_day")
	private String arriveDay;

	@JsonProperty("arrive_time")
	private String arriveTime;

	private int distance;
	private String platform;
	
	private Geo geo;

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getDepartDay() {
		return departDay;
	}

	public void setDepartDay(String departDay) {
		this.departDay = departDay;
	}

	public String getDepartTime() {
		return departTime;
	}

	public void setDepartTime(String departTime) {
		this.departTime = departTime;
	}

	public String getStoppingTime() {
		return stoppingTime;
	}

	public void setStoppingTime(String stoppingTime) {
		this.stoppingTime = stoppingTime;
	}

	public String getArriveDay() {
		return arriveDay;
	}

	public void setArriveDay(String arriveDay) {
		this.arriveDay = arriveDay;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Geo getGeo() {
		return geo;
	}

	public void setGeo(Geo geo) {
		this.geo = geo;
	}

}
