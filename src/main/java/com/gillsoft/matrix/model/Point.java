package com.gillsoft.matrix.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class Point extends Locality {
	
	private static final long serialVersionUID = 2990791120758116405L;
	
	@JsonProperty("native_address")
	private String nativeAddress;

	public String getNativeAddress() {
		return nativeAddress;
	}

	public void setNativeAddress(String nativeAddress) {
		this.nativeAddress = nativeAddress;
	}

}
