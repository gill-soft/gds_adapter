package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Route implements Serializable {

	private static final long serialVersionUID = -7618751937249190787L;
	
	private String code;
	private String regularity;
	
	@JsonProperty("regularity_days")
	private List<String> regularityDays;
	
	@JsonProperty("carrier_code")
	private String carrierCode;
	
	@JsonProperty("sale_close_before")
	private int saleCloseBefore;
	
	@JsonProperty("reservation_close_before")
	private int reservationCloseBefore;
	
	@JsonProperty("is_test")
	private boolean test;
	
	@JsonProperty("started_at")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date started;
	
	@JsonProperty("ended_at")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date ended;
	
	private String type;
	private String kind;
	
	@JsonProperty("return_policy")
	private String returnPolicy;
	
	@JsonProperty("baggage_policy")
	private String baggagePolicy;
	
	private Parameters carrier;
	
	private List<PathPoint> path;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRegularity() {
		return regularity;
	}

	public void setRegularity(String regularity) {
		this.regularity = regularity;
	}

	public List<String> getRegularityDays() {
		return regularityDays;
	}

	public void setRegularityDays(List<String> regularityDays) {
		this.regularityDays = regularityDays;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public int getSaleCloseBefore() {
		return saleCloseBefore;
	}

	public void setSaleCloseBefore(int saleCloseBefore) {
		this.saleCloseBefore = saleCloseBefore;
	}

	public int getReservationCloseBefore() {
		return reservationCloseBefore;
	}

	public void setReservationCloseBefore(int reservationCloseBefore) {
		this.reservationCloseBefore = reservationCloseBefore;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getEnded() {
		return ended;
	}

	public void setEnded(Date ended) {
		this.ended = ended;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getReturnPolicy() {
		return returnPolicy;
	}

	public void setReturnPolicy(String returnPolicy) {
		this.returnPolicy = returnPolicy;
	}

	public String getBaggagePolicy() {
		return baggagePolicy;
	}

	public void setBaggagePolicy(String baggagePolicy) {
		this.baggagePolicy = baggagePolicy;
	}

	public Parameters getCarrier() {
		return carrier;
	}

	public void setCarrier(Parameters carrier) {
		this.carrier = carrier;
	}

	public List<PathPoint> getPath() {
		return path;
	}

	public void setPath(List<PathPoint> path) {
		this.path = path;
	}

}
