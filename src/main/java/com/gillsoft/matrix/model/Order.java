package com.gillsoft.matrix.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gillsoft.model.Document;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Order {
	
	private String link;
	private String number;
	private String status;
	
	@JsonProperty("sale_date")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleDate;
	
	@JsonProperty("reserved_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservedTo;
	
	private String phone;
	private String email;
	private BigDecimal cost;
	private BigDecimal price;
	
	@JsonProperty("reservation_enable_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservationEnableTo;
	
	@JsonProperty("sale_enable_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleEnableTo;
	
	private String hash;
	private String currency;

	private Map<String, List<Ticket>> tickets;
	
	@JsonIgnore
	private List<Document> documents;

	public Order() {

	}

	public Order(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public Date getReservedTo() {
		return reservedTo;
	}

	public void setReservedTo(Date reservedTo) {
		this.reservedTo = reservedTo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getReservationEnableTo() {
		return reservationEnableTo;
	}

	public void setReservationEnableTo(Date reservationEnableTo) {
		this.reservationEnableTo = reservationEnableTo;
	}

	public Date getSaleEnableTo() {
		return saleEnableTo;
	}

	public void setSaleEnableTo(Date saleEnableTo) {
		this.saleEnableTo = saleEnableTo;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Map<String, List<Ticket>> getTickets() {
		return tickets;
	}

	public void setTickets(Map<String, List<Ticket>> tickets) {
		this.tickets = tickets;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
}
