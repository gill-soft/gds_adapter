package com.gillsoft.matrix.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ReturnRule {
	
	private String title;
	private String description;
	private int minutesBeforeDepart;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date activeTo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMinutesBeforeDepart() {
		return minutesBeforeDepart;
	}

	public void setMinutesBeforeDepart(int minutesBeforeDepart) {
		this.minutesBeforeDepart = minutesBeforeDepart;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

}
