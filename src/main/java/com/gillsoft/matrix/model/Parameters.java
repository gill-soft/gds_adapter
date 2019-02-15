package com.gillsoft.matrix.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Parameters implements Serializable {

	private static final long serialVersionUID = -6838450891436102310L;
	
	private String name;
	private String address;
	private String description;
	
	public Parameters(Object name, Object address, Object description) {
		this.name = name != null ? name.toString() : null;
		this.address = address != null ? address.toString() : null;
		this.description = description != null ? description.toString() : null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
