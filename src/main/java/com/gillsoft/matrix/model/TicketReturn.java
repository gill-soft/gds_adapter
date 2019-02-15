package com.gillsoft.matrix.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class TicketReturn {

	private String type;
	private String currency;
	private BigDecimal amount;
	private String description;
	
	@JsonProperty("exchange_rate")
	private BigDecimal exchangeRate;
	
	@JsonProperty("carrier_tariff")
	private BigDecimal carrierTariff;
	
	private BigDecimal vat;
	
	@JsonProperty("return_rule")
	private ReturnRule rule;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getCarrierTariff() {
		return carrierTariff;
	}

	public void setCarrierTariff(BigDecimal carrierTariff) {
		this.carrierTariff = carrierTariff;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public ReturnRule getRule() {
		return rule;
	}

	public void setRule(ReturnRule rule) {
		this.rule = rule;
	}

}
