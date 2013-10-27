package com.eventshero.api.model;

import com.google.gson.annotations.Expose;

public class CalendarTag extends ModelBase {
	@Expose private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}