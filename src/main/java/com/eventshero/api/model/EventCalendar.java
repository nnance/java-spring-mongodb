package com.eventshero.api.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.eventshero.api.model.parsing.CalendarParser;
import com.eventshero.api.model.parsing.ParserManager;

public class EventCalendar extends ModelBase {
	@Expose private String description;
	@Expose private String location;
	@Expose private String timeZone = "US/Eastern";
	@Expose private Boolean ignoreClientTimeZone = false;
	@Expose private String webUrl;
	@Expose private String logoUrl;
	@Expose private Boolean publicCalendar = false;
	@Expose private Boolean webSync = false;
	@Expose private String webSyncParser;
	@Expose private String teamId;
	@Expose private Set<String> tagKeys;
    @Expose private Set<CalendarItem> items;
    @Expose private String source;
    @Expose private Set<CalendarTag> tags;

	public EventCalendar() {
		this.tagKeys = new HashSet<String>();
		this.tags = new HashSet<CalendarTag>();
		this.items = new HashSet<CalendarItem>();
	}

	public boolean addTag(CalendarTag tag) {
		boolean result = true;
		this.tags.add(tag);
		if (tag.getId() != null)
			result = tagKeys.add(tag.getId());
		return result;
	}
	
	public boolean addTagKey(String key) {
		return tagKeys.add(key);
	}
	
	public void removeTag(CalendarTag tag) {
		tagKeys.remove(tag.getId());
	}
	
	public Set<String> getTagKeys() {
		if (tagKeys == null)
			tagKeys = new HashSet<String>();
		return tagKeys;
	}
	
	public CalendarTag getTag(int index) {
		CalendarTag result = null;
		
		if (index < this.tags.size())
			result = (CalendarTag) this.tags.toArray()[index];
		return result;
	}
	
	public boolean addItem(CalendarItem item) {
		return items.add(item);
	}
	
	public void removeItem(CalendarItem item) {
		for (CalendarItem calItem : items) {
			if (calItem.getId().equals(item.getId())) {
				items.remove(calItem);
				break;
			}
		}		
	}
	
	public CalendarItem containsItem(String calId) {
		CalendarItem result = null;
		for (CalendarItem item : this.getItems()) {
			if (item.getCalId().equals(calId)) {
				result = item;
				break;
			}
		}
		return result;
		
	}
	
	public boolean sync() throws Exception {
		if (this.getWebSync()) {
			String parser = this.getWebSyncParser();		
			System.out.println("Calendar Sync started - " + this.getName());
			CalendarParser webParser = ParserManager.getParser(parser);
			Set<CalendarItem> items = webParser.loadCalendar(this);
			System.out.println("items found: " + items.size());
			for (CalendarItem item : items) {
				CalendarItem exists = this.containsItem(item.getCalId());
				if (exists != null) 
					exists.merge(item);
				else
					this.addItem(item);
			}
			System.out.println("Calendar Sync completed");
		}
		return true;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTimeZone() {
		if ((timeZone == null) || (timeZone.length() == 0))
			timeZone = "US/Eastern";
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public Boolean getPublicCalendar() {
		return publicCalendar;
	}
	public void setPublicCalendar(Boolean publicCalendar) {
		this.publicCalendar = publicCalendar;
	}
	public Boolean getWebSync() {
		return webSync;
	}
	public void setWebSync(Boolean webSync) {
		this.webSync = webSync;
	}
	public String getWebSyncParser() {
		return webSyncParser;
	}
	public void setWebSyncParser(String webSyncParser) {
		this.webSyncParser = webSyncParser;
	}
	public Set<CalendarItem> getItems() {
		if (items == null)
			items = new HashSet<CalendarItem>();
		return items;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String tid) {
		this.teamId = tid;
	}

	public Boolean getIgnoreClientTimeZone() {
		return ignoreClientTimeZone;
	}

	public void setIgnoreClientTimeZone(Boolean ignoreClientTimeZone) {
		this.ignoreClientTimeZone = ignoreClientTimeZone;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
