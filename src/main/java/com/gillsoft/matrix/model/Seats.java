package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Seats implements Serializable {
	
	private static final long serialVersionUID = -783011474515065722L;
	
	private Map<String, String> list;
	private int count;
	
	@JsonProperty("is_open")
	private boolean open;

	public Map<String, String> getList() {
		return list;
	}

	public void setList(Map<String, String> list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
}
