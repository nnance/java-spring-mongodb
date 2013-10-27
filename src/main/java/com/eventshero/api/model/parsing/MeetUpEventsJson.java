package com.eventshero.api.model.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;
import com.eventshero.api.model.GeoLocation;
import com.eventshero.api.model.parsing.MeetUpEventsJson.MeetUpResponse.MeetUpEvent;

public class MeetUpEventsJson {
	private static final String APIKEY = "3964d2e785b345f6529717151763859";
	private static final String CALPREFIX = "mu";
	
	public static class MeetUpResponse {
		public List<MeetUpEvent> results;
		
		public static class MeetUpEvent {
			public String id;
			public long utc_offset;
			public long time;
			public String name;
			public String description;
			public String event_url;
			public String how_to_find_us;
			public MeetUpVenue venue;
			public MeetUpGroup group;
		}
		
		public static class MeetUpVenue {
			public String id;
			public String name;
		}
		
		public static class MeetUpCategory {
			public String id;
			public String name;
			public String shortname;
		}
		
		public static class MeetUpGroup {
			public String id;
			public String name;
			public MeetUpCategory category;
		}
	}
		
	protected String doGet(String url) throws IOException {
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.setRequestProperty("Accept", "application/json");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String line = "";
		while((line=in.readLine())!=null) {
			result = result + line + "\n";
		}
		in.close();
		return result;
	}
	
	protected MeetUpResponse getEvents(String url) throws IOException {
		String json = doGet(url);
		MeetUpResponse events = new Gson().fromJson(json, MeetUpResponse.class);
		return events;
	}

	public List<EventFeed> getEventFeed(String feedId, String zoneName) throws Exception {
	    List<EventFeed> feeds = new ArrayList<EventFeed>();
	    EventCalendar cal = getCalendarById(feedId,zoneName);
	    if (cal != null) {
	    	List<EventCalendar> cals = new ArrayList<EventCalendar>();
	    	cals.add(cal);
	    	feeds = EventFeed.getFeed(cals, zoneName, true);
	    }
	    return feeds;
	}
	
	private EventCalendar getCalendarById(String feedId, String zoneName) throws ParseException {
		MeetUpResponse meetupContent = null;
		try {
		    if (feedId.contains(CALPREFIX)) 
		    	meetupContent = this.getEvents("https://api.meetup.com/2/events?&sign=true&key=" + APIKEY + "&group_id="+feedId.split("-")[1]+"&page=20");
		} catch (IOException e) {
			e.printStackTrace();
		}			
		if (meetupContent != null) {
			List<EventCalendar> cals = processContent(meetupContent, zoneName,null);
			if (cals.size() == 1)
				return cals.get(0);
			else
				return null;
		}
		else 
			return null;

	}

	public List<EventCalendar> getCalendarList(GeoLocation geoLoc, String location, String feedId, String zoneName, int limit, int radius, String source) throws ParseException {
		MeetUpResponse meetupContent = null;
		if (geoLoc != null) {
			location = "lat="+geoLoc.getLatitude() + "&lon=" + geoLoc.getLongitude();
		}
		if (location == null) 
			location = "state=ok&city=norman&zip=73072";
		try {
			meetupContent = this.getEvents("https://api.meetup.com/2/open_events?&sign=true&fields=category&key=" + APIKEY + "&" +location + "&page="+limit+"&radius="+radius);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processContent(meetupContent, zoneName, source);
	}
	
	private List<EventCalendar> processContent(MeetUpResponse meetupContent, String zoneName, String source) {
		List<EventCalendar> result = new ArrayList<EventCalendar>();
		
		if (meetupContent != null) {
			for (MeetUpEvent event : meetupContent.results) {
				EventCalendar cal = new EventCalendar();
				cal.setName(event.group.name);
				cal.setTimeZone(zoneName);
				cal.setId(CALPREFIX+"-"+event.group.id);
				cal.setSource(source);
				String groupCat = event.group.category.shortname.toLowerCase();
				if (groupCat.equals("films"))
					cal.setLogoUrl("movies");
				else if (groupCat.equals("arts"))
					cal.setLogoUrl("theatre");
				else if (groupCat.contains("sports"))
					cal.setLogoUrl("sports");
				else if (groupCat.contains("dancing"))
					cal.setLogoUrl("live-music");
				else if (groupCat.contains("music"))
					cal.setLogoUrl("music");
				else if (groupCat.contains("fasion"))
					cal.setLogoUrl("shopping");
				else if (groupCat.contains("business") || groupCat.contains("tech"))
					cal.setLogoUrl("technology");
				else if (groupCat.contains("lifestyle"))
					cal.setLogoUrl("lifestyle");
				else if (groupCat.contains("fitness"))
					cal.setLogoUrl("exercise");
				else 
					cal.setLogoUrl("meetup");
				boolean hasCal = false;
				for (EventCalendar existCal : result)
					if (existCal.getId().equals(cal.getId())) {
						hasCal = true;
						break;
					}
				if (!hasCal) result.add(cal);
			}
			TimeZone utcZone = TimeZone.getTimeZone("UTC");
			SimpleDateFormat fromWeb = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			fromWeb.setTimeZone(utcZone);
			SimpleDateFormat myDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat myTimeFormat = new SimpleDateFormat("hh:mm a");
			for (MeetUpEvent event : meetupContent.results) {
				for (EventCalendar cal : result) 
					if (cal.getId().contains(event.group.id)) {
						CalendarItem item = new CalendarItem();
						item.setName(event.name);
						item.setDescription(event.description);
						Calendar calStartTime = new GregorianCalendar();
						calStartTime.setTimeInMillis(event.utc_offset);
						calStartTime.setTimeInMillis(event.time);
						item.setStartDate(myDateFormat.format(calStartTime.getTime()));
						item.setStartTime(myTimeFormat.format(calStartTime.getTime()));
						cal.addItem(item);
					}
			}
		}

		return result;
	}

	public static void main (String [] args) {
		MeetUpEventsJson events = new MeetUpEventsJson();
		try {
			List<EventCalendar> cals = events.getCalendarList(null,"Norman,OK",null,"CST",100,30,"nearby");
			for (EventCalendar cal : cals) {
				System.out.println("Calendar: " + cal.getName());
				for (CalendarItem item : cal.getItems()) {
					System.out.println("Event: " + item.getName());
					System.out.println("Date: " + item.getStartDate());
					System.out.println("Time: " + item.getStartTime());
					System.out.println("Description: " + item.getDescription());
				}
				System.out.println();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
}
