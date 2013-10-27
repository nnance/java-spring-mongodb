package com.eventshero.api.dao;

import java.util.List;
import java.util.Set;

import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;

public interface CalendarCatalogDao extends GenericDao<CalendarCatalog> {
	public EventCalendar syncCalendarInList(String id, EventCalendar cal);
	public Set<EventCalendar> syncRemove(String id);
	
	public List<CalendarTag> getTags(String id);
	public CalendarTag addTag(String id, String tagId);
	public CalendarTag removeTag(String id, String tagId);

	public Set<EventCalendar> getItems(String id);
}
