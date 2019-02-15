package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Fee implements Serializable {
	
	private static final long serialVersionUID = -7822758089763107985L;
	
	private String id;
	private String kind;
	private String type;

	@JsonProperty("limit_from")
	private int limitFrom;

	@JsonProperty("limit_to")
	private int limitTo;

	private BigDecimal value;
	
	@JsonProperty("is_in_path_tariff")
    private boolean inPathTariff;
    
	private String from;
	private BigDecimal amount;
	private String key;
	private String name;
	
	@JsonProperty("is_in_carrier_tariff")
	private boolean inCarrierTariff;
	
	@JsonProperty("is_from_base")
	private boolean fromBase;
	
	private String currency;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLimitFrom() {
		return limitFrom;
	}

	public void setLimitFrom(int limitFrom) {
		this.limitFrom = limitFrom;
	}

	public int getLimitTo() {
		return limitTo;
	}

	public void setLimitTo(int limitTo) {
		this.limitTo = limitTo;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public boolean isInPathTariff() {
		return inPathTariff;
	}

	public void setInPathTariff(boolean inPathTariff) {
		this.inPathTariff = inPathTariff;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInCarrierTariff() {
		return inCarrierTariff;
	}

	public void setInCarrierTariff(boolean inCarrierTariff) {
		this.inCarrierTariff = inCarrierTariff;
	}

	public boolean isFromBase() {
		return fromBase;
	}

	public void setFromBase(boolean fromBase) {
		this.fromBase = fromBase;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
