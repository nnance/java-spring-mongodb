package com.eventshero.api.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class CalendarCatalog extends ModelBase {
	@Expose private String description;
	@Expose private String webUrl;
	@Expose private String webSyncParser;
	@Expose private Set<String> tagKeys;
	@Expose private Set<EventCalendar> calendars;

	public CalendarCatalog() {
		tagKeys = new HashSet<String>();
	}
	
	public EventCalendar syncCalendarInList(EventCalendar cal) {
		// determine if we need to add or update an existing calendar
		EventCalendar exists = null;
		Set<EventCalendar> calendars = this.getCalendars();
		for (EventCalendar obj : calendars) {
			if (obj.getName().equals(cal.getName())) {
				exists = obj;
				break;
			}
		}
		if (exists != null) {
			exists.setWebSyncParser(this.getWebSyncParser());
			exists.setDescription(cal.getDescription());
			exists.setLocation(cal.getLocation());
			if ((cal.getLogoUrl() != null) && (cal.getLogoUrl().length() > 0))
				exists.setLogoUrl(cal.getLogoUrl());
			Set<String> tags = exists.getTagKeys();
			if (tags.size() == 0)
				addTagsToCalendar(exists);
			return exists;
		}
		else {
			addTagsToCalendar(cal);
			this.calendars.add(cal);
			return cal;
		}
		
	}
	
	private void addTagsToCalendar(EventCalendar cal) {
		Set<String> tagKeys = this.getTagKeys();
		for (String key : tagKeys) {
			cal.addTagKey(key);
		}		
	}
	
	public void syncRemove() {
		calendars.removeAll(calendars);
	}
	
	public boolean addTag(CalendarTag tag) {
		return tagKeys.add(tag.getId());
	}
	
	public void removeTag(CalendarTag tag) {
		tagKeys.remove(tag.getId());
	}
	
	public Set<String> getTagKeys() {
		if (tagKeys == null)
			tagKeys = new HashSet<String>();
		return tagKeys;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public String getWebSyncParser() {
		return webSyncParser;
	}
	public void setWebSyncParser(String webSyncParser) {
		this.webSyncParser = webSyncParser;
	}
	public Set<EventCalendar> getCalendars() {
		if (calendars == null) 
			calendars = new HashSet<EventCalendar>();
		return calendars;
	}

}
