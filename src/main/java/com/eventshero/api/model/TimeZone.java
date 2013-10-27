package com.eventshero.api.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class TimeZone {
	@Expose private String id;
	@Expose private String displayName;

	public TimeZone(String id,String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public static Set<TimeZone> getList() throws Exception {
		String[] ids = java.util.TimeZone.getAvailableIDs();
		Set<TimeZone> zones = new HashSet<TimeZone>();
		//load the timezones for US only
		for (int i = 3; i < ids.length; i++) {
			if (ids[i].contains("US")) {
				zones.add(new TimeZone(ids[i], ids[i]));
			}
		}
		return zones;
	}
	
	public static TimeZone getTimeZone(String id) throws Exception {
		java.util.TimeZone c = java.util.TimeZone.getTimeZone(id);
		if(c==null)
			throw new RuntimeException("getTimeZone: TimeZone with " + id +  " not found");
		TimeZone exists = new TimeZone(c.getID(),c.getDisplayName());
		return exists;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
