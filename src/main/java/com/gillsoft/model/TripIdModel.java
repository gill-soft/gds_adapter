package com.gillsoft.model;

public class TripIdModel extends AbstractJsonModel {

	private static final long serialVersionUID = -5947058557176141198L;

	private long resourceId;

	public TripIdModel() {

	}

	public TripIdModel(long resourceId) {
		this.resourceId = resourceId;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public TripIdModel create(String json) {
		return (TripIdModel) super.create(json);
	}
}
