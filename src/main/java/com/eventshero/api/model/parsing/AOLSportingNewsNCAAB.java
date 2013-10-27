package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;

public class AOLSportingNewsNCAAB extends AOLSportingNews {

	@Override
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws IOException, ParseException {
		Set<EventCalendar> cals = super.getCalendarList(cat, tags);
		for (EventCalendar cal : cals) {
			cal.setName(cal.getName() + " Basketball");
			cal.setWebUrl(cal.getWebUrl() + "?season=2012");
		}
		return cals;
	}
}
