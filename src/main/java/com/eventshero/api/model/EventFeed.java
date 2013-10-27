package com.eventshero.api.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.annotations.Expose;

public class EventFeed implements Comparable<EventFeed> {
	@Expose public String id;
	@Expose public String day;
	@Expose public String sortDay;
	@Expose public String date;
	@Expose public String time;
	@Expose public String title;
	@Expose public String description;
	@Expose public String location;
	@Expose public String location2;
	@Expose public String info;
	@Expose public String calendarName;
	@Expose public String calendarDescription;
	@Expose public String calendarLogo;
	@Expose public String calendarId;
	@Expose public String calendarSource;
	@Expose public String teamName;
	@Expose public String opponentName;
	@Expose public String teamScore;
	@Expose public String opponentScore;
	@Expose public String recapUrl;
	@Expose public String boxScoreUrl;
	@Expose public String ticketUrl;
	private static final Logger log = Logger.getLogger(EventFeed.class.getName());

	public static List<EventFeed> getFeed(List<EventCalendar> cals, String zoneName, boolean fullFeed) throws Exception {
		List<EventFeed> feed = null;
		Calendar minDate = Calendar.getInstance();
//		minDate.add(Calendar.MONTH, -1);
		Calendar maxDate = Calendar.getInstance();
		maxDate.add(Calendar.DATE, 45);
		if (!fullFeed) 
			feed = getFeed(cals,zoneName, minDate, maxDate);
		else
			feed = getFeed(cals,zoneName,null,null);
		return feed;
	}
	
	public static List<EventFeed> getFeedAtStartDate(List<EventCalendar> cals, String zoneName, String startDate) throws Exception {
		List<EventFeed> feed = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date parseDate = dateFormat.parse(startDate);
		Calendar minDate = Calendar.getInstance();
		minDate.setTime(parseDate);
		Calendar maxDate = Calendar.getInstance();
		maxDate.setTime(parseDate);
		maxDate.add(Calendar.DATE, 45);
		feed = getFeed(cals,zoneName, minDate, maxDate);
		return feed;
	}
	
	public static List<EventFeed> getFeed(List<EventCalendar> cals, String zoneName, Calendar minDate, Calendar maxDate) throws Exception {
		List<EventFeed> result = new ArrayList<EventFeed>();
		boolean fullFeed = false;

		if ((minDate == null) && (maxDate == null))
			fullFeed = true;
		else {
			minDate.set(Calendar.HOUR_OF_DAY, 0);
			minDate.set(Calendar.MINUTE, 0);
			minDate.set(Calendar.SECOND, 0);
			maxDate.set(Calendar.HOUR_OF_DAY, 23);
			maxDate.set(Calendar.MINUTE, 59);
			maxDate.set(Calendar.SECOND, 59);
		}
		log.info("calendar count: " + cals.size());
		for (EventCalendar cal : cals) {
			if (zoneName == null) 
				zoneName = "America/Baltimore";
			java.util.TimeZone timeZone = java.util.TimeZone.getTimeZone(zoneName);				
			if (cal.getItems() != null) {
//				log.info("cal name: " + cal.getName() + " item count: " + cal.getItems().size());
				for (CalendarItem item : cal.getItems()) {
	
					if ((timeZone == null) || ((cal.getIgnoreClientTimeZone() != null) && (cal.getIgnoreClientTimeZone()))) 
						timeZone = java.util.TimeZone.getTimeZone(cal.getTimeZone());
					
					SimpleDateFormat toWeb = new SimpleDateFormat("EEE, MMM dd");
					toWeb.setTimeZone(timeZone);
					SimpleDateFormat startFormat = new SimpleDateFormat("h:mm a");
					startFormat.setTimeZone(timeZone);
	
					EventFeed feed = new EventFeed();
					Calendar dateTime = item.getDateTime(java.util.TimeZone.getTimeZone(cal.getTimeZone()));
					try {
						feed.id = item.getId();
						feed.sortDay = item.getStartDateTime(dateTime, timeZone);
						feed.day = toWeb.format(dateTime.getTime());
						feed.calendarName = cal.getName();
						feed.calendarDescription = cal.getDescription();
						if ((cal.getLogoUrl() != null) && (cal.getLogoUrl().length() > 0)) {
							if (cal.getLogoUrl().contains("http"))
								feed.calendarLogo = "default";
							else
								feed.calendarLogo = cal.getLogoUrl();
						}
						feed.calendarId = cal.getId();
						feed.calendarSource = cal.getSource();
						feed.date = item.getStartDate();
						if (item.isAllDayEvent())
							feed.time = "All day";
						else 
							feed.time = startFormat.format(dateTime.getTime());
						feed.title = item.getName();
						feed.location = item.getLocation();
						feed.location2 = item.getLocation2();
						feed.description = item.getDescription();
						feed.info = item.getInfo();

						feed.teamName = item.getTeamName();
						feed.opponentName = item.getOpponentName();
						feed.teamScore = item.getTeamScore();
						feed.opponentScore = item.getOpponentScore();
						feed.recapUrl = item.getRecapUrl();
						feed.boxScoreUrl = item.getBoxScoreUrl();
						feed.ticketUrl = item.getTicketUrl();
						
						if (fullFeed)
							result.add(feed);
						else if ((dateTime.compareTo(minDate) > 0)  && (dateTime.compareTo(maxDate) < 0)) {
							result.add(feed);
						}
					} catch (Exception e) {
						log.log(Level.SEVERE, e.toString(), e);
					}
	
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public int compareTo(EventFeed compareEvent) {
		return this.sortDay.compareTo(compareEvent.sortDay);
	}

	
}
