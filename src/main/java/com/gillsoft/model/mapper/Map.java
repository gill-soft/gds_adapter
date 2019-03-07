package com.gillsoft.model.mapper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Map implements Serializable {

	private static final long serialVersionUID = -1052211220608317433L;

	@JsonProperty("parent_id")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}