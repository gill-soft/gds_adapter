package com.gillsoft.matrix.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Locale {

	private String name;
	
	@JsonProperty("native")
	private String nativeName;
	
	public Locale() {

	}

	public Locale(String name, String nativeName) {
		this.name = name;
		this.nativeName = nativeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Locale)) {
			return false;
		}
		Locale locale = (Locale) obj;
		return Objects.equals(nativeName, locale.getNativeName())
				&& Objects.equals(name, locale.getName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, nativeName);
	}
	
}
