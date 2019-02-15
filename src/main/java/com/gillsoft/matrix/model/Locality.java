package com.gillsoft.matrix.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Locality extends City {

	private static final long serialVersionUID = -4074867855135159899L;
	
	private Map<String, Parameters> i18n;

	public Map<String, Parameters> getI18n() {
		return i18n;
	}

	public void setI18n(Map<String, Parameters> i18n) {
		this.i18n = i18n;
	}
	
}
