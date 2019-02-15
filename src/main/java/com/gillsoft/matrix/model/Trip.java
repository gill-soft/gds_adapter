package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Trip implements Serializable {
	
	private static final long serialVersionUID = -3015444050776850171L;
	
	private String tripId;
	private String routeId;
	private String routeCombinedId;
	private String routeCode;
	private String carrier;
	private String carrierName;
	private String intervalId;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date departDate;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date arriveDate;
	
	private String arriveTime;
	private String departTime;
	private String departCity;
	private String departCityId;
	private String arriveCity;
	private String arriveCityId;
	private String carrierCode;
	private String busNumber;
	private String departStation;
	private String departStationId;
	private String arriveStation;
	private String arriveStationId;
	private Seats freeSeats;
	private int distance;
	private String timeInWay;
	private boolean international;
	private Map<String, Boolean> docFields;
	private BigDecimal tariff;
	private Price price;
	private List<Discount> discounts;
	private String currency;
	
	@JsonProperty("exchange_rate")
	private float exchangeRate;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleCloseAt;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservationCloseAt;
	
	private List<String> tags;

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteCombinedId() {
		return routeCombinedId;
	}

	public void setRouteCombinedId(String routeCombinedId) {
		this.routeCombinedId = routeCombinedId;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getIntervalId() {
		return intervalId;
	}

	public void setIntervalId(String intervalId) {
		this.intervalId = intervalId;
	}

	public Date getDepartDate() {
		return departDate;
	}

	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getDepartTime() {
		return departTime;
	}

	public void setDepartTime(String departTime) {
		this.departTime = departTime;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getDepartCityId() {
		return departCityId;
	}

	public void setDepartCityId(String departCityId) {
		this.departCityId = departCityId;
	}

	public String getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(String arriveCity) {
		this.arriveCity = arriveCity;
	}

	public String getArriveCityId() {
		return arriveCityId;
	}

	public void setArriveCityId(String arriveCityId) {
		this.arriveCityId = arriveCityId;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public String getDepartStation() {
		return departStation;
	}

	public void setDepartStation(String departStation) {
		this.departStation = departStation;
	}

	public String getDepartStationId() {
		return departStationId;
	}

	public void setDepartStationId(String departStationId) {
		this.departStationId = departStationId;
	}

	public String getArriveStation() {
		return arriveStation;
	}

	public void setArriveStation(String arriveStation) {
		this.arriveStation = arriveStation;
	}

	public String getArriveStationId() {
		return arriveStationId;
	}

	public void setArriveStationId(String arriveStationId) {
		this.arriveStationId = arriveStationId;
	}

	public Seats getFreeSeats() {
		return freeSeats;
	}

	public void setFreeSeats(Seats freeSeats) {
		this.freeSeats = freeSeats;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getTimeInWay() {
		return timeInWay;
	}

	public void setTimeInWay(String timeInWay) {
		this.timeInWay = timeInWay;
	}

	public boolean isInternational() {
		return international;
	}

	public void setInternational(boolean international) {
		this.international = international;
	}

	public Map<String, Boolean> getDocFields() {
		return docFields;
	}

	public void setDocFields(Map<String, Boolean> docFields) {
		this.docFields = docFields;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public void setTariff(BigDecimal tariff) {
		this.tariff = tariff;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public float getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getSaleCloseAt() {
		return saleCloseAt;
	}

	public void setSaleCloseAt(Date saleCloseAt) {
		this.saleCloseAt = saleCloseAt;
	}

	public Date getReservationCloseAt() {
		return reservationCloseAt;
	}

	public void setReservationCloseAt(Date reservationCloseAt) {
		this.reservationCloseAt = reservationCloseAt;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
