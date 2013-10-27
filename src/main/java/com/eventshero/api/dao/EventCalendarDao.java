package com.eventshero.api.dao;

import java.util.List;
import java.util.Set;

import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;

public interface EventCalendarDao extends GenericDao<EventCalendar> {

	public List<EventCalendar> getCalendarsById(Set<String> ids, int fetchDepth);
	public List<EventCalendar> getCalendarsByTag(String tag);
	public List<EventFeed> getEventFeed(String feedId, String zoneName) throws Exception;

	public List<CalendarTag> getTags(String id);
	public CalendarTag addTag(String id, String tagId);
	public CalendarTag removeTag(String id, String tagId);

	public Set<CalendarItem> getItems(String id);
	public CalendarItem addItem(String id, CalendarItem item);
	public CalendarItem removeItem(String id, String itemId);
	
	public EventCalendar sync(String id) throws Exception;
	
}
