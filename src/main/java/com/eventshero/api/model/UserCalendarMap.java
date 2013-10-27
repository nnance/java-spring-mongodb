package com.eventshero.api.model;

public class UserCalendarMap extends ModelBase {
	private boolean liked = false;
	private String calendarKey;
	
	public String getCalendarKey() {
		return calendarKey;
	}
	public void setCalendar(EventCalendar calendar) {
		calendarKey = calendar.getId();
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}

}
