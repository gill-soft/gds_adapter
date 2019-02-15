package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Country implements Serializable {

	private static final long serialVersionUID = -4604416579211450039L;
	
	private int id;
	private String iso;
	private String locale;

	@JsonProperty("native_name")
	private String nativeName;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Country)) {
			return false;
		}
		Country country = (Country) obj;
		return id == country.getId()
				&& Objects.equals(iso, country.getIso())
				&& Objects.equals(locale, country.getLocale())
				&& Objects.equals(nativeName, country.getNativeName())
				&& Objects.equals(name, country.getName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, iso, locale, nativeName, name);
	}

}
