package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Price implements Serializable {
	
	private static final long serialVersionUID = 7503673018700582892L;

	@JsonProperty("one_way")
	private BigDecimal oneWay;
	
	@JsonProperty("round_trip")
	private BigDecimal roundTrip;
	
	public Price() {

	}

	public Price(BigDecimal oneWay, BigDecimal roundTrip) {
		this.oneWay = oneWay;
		this.roundTrip = roundTrip;
	}

	public BigDecimal getOneWay() {
		return oneWay;
	}

	public void setOneWay(BigDecimal oneWay) {
		this.oneWay = oneWay;
	}

	public BigDecimal getRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(BigDecimal roundTrip) {
		this.roundTrip = roundTrip;
	}

}
