package com.eventshero.api.model.parsing;

import java.util.Set;

import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.CalendarCatalog;

public abstract class CalendarParser {
	public abstract Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws Exception;
	public abstract Set<CalendarItem> loadCalendar(EventCalendar cal) throws Exception;
}
