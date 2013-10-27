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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.EventFeed;
import com.eventshero.api.model.GeoLocation;
import com.eventshero.api.model.parsing.ZVentsJson.ZVents.ZVentsResponse.ZVentsContent;
import com.eventshero.api.model.parsing.ZVentsJson.ZVents.ZVentsResponse.ZVentsContent.ZVentsEvent;
import com.eventshero.api.model.parsing.ZVentsJson.ZVents.ZVentsResponse.ZVentsContent.ZVentsVenue;

public class ZVentsJson {
	private static final String APIKEY = "NMWDEUDYCCADOARQBRJTKBHWRKPMBGLTQTOFTMKCBSQOJOQNAESOOVOJYNVEVFJG";
	private static final String EVENTFIELDS = "venue.name,venue.id,venue.description,venue.types";
	private static final String CALPREFIX = "zv";
	
	public static class ZVents {
		public ZVentsResponse rsp;
		
		public static class ZVentsResponse {
			public String status;
			public ZVentsContent content;	
	
			public static class ZVentsContent {
				public List<ZVentsEvent> events;
				public List<ZVentsVenue> venues;
	
				public static class ZVentsEvent {
					public String id;
					public String name;
					public String price;
					public boolean editors_pick;
					public String url;
					public String description;
					public String vid;
					public String startTime;
					public String endTime;
					public String zurl;
				}
				
				public static class ZVentsVenue {
					public String id;
					public String name;
					public String description;
					public String[] types;
				}
			}
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
	
	protected ZVentsContent getEvents(String url) throws IOException {
		String json = doGet(url);
		ZVents zvents = new Gson().fromJson(json, ZVents.class);
		return zvents.rsp.content;
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
		ZVentsContent zContent = null;
		try {
		    if (feedId.contains(CALPREFIX)) 
		    	zContent = this.getEvents("http://www.zvents.com/partner_rest/venue_events?key="+APIKEY+"&format=json&fields="+EVENTFIELDS+"&limit=20&id=" + feedId.split("-")[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		if (zContent != null) {
			List<EventCalendar> cals = processContent(zContent, zoneName, null);
		
			if (cals.size() == 1)
				return cals.get(0);
			else
				return null;
		}
		else return null;

	}

	public List<EventCalendar> getCalendarList(GeoLocation geoLoc, String location, String feedId, String zoneName, int limit, int radius, String source) throws ParseException {		
		ZVentsContent zContent = null;
		
		if (geoLoc != null) {
			location = geoLoc.getLongitude() + ":BY:" + geoLoc.getLatitude();
		}
		if (location == null) 
			location = "SanFrancisco,CA";
		
		try {
			zContent = this.getEvents("http://www.zvents.com/partner_rest/search?key="+APIKEY+"&format=json&fields="+EVENTFIELDS+"&limit="+limit+"&where=" + location + "&radius=" + radius);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return processContent(zContent, zoneName, source);
	}
		
	private List<EventCalendar> processContent(ZVentsContent zContent, String zoneName, String source) throws ParseException {		
		List<EventCalendar> result = new ArrayList<EventCalendar>();

		if (zContent != null) {
			for (ZVentsVenue venue : zContent.venues) {
				EventCalendar cal = new EventCalendar();
				cal.setName(venue.name);
				cal.setDescription(venue.description);
				cal.setTimeZone(zoneName);
				cal.setSource(source);
				cal.setId("zv-"+venue.id);
				if (venue.types.length > 0) {
					String vType = venue.types[0].toLowerCase();
					if (vType.equals("movie theater"))
						cal.setLogoUrl("movies");
					else if (vType.equals("theater"))
						cal.setLogoUrl("theatre");
					else if (vType.contains("stadium") || vType.contains("track"))
						cal.setLogoUrl("sports");
					else if (vType.contains("bar") || vType.contains("auditorium") || vType.contains("nightclub") || vType.contains("amphitheater") || vType.contains("adult club"))
						cal.setLogoUrl("live-music");
					else if (vType.contains("arts") || vType.contains("ballroom"))
						cal.setLogoUrl("music");
					else if (vType.contains("shopping"))
						cal.setLogoUrl("shopping");
					else 
						cal.setLogoUrl("technology");
				}
				result.add(cal);
			}
			TimeZone utcZone = TimeZone.getTimeZone("UTC");
			TimeZone tZone = TimeZone.getTimeZone(zoneName);
			SimpleDateFormat fromWeb = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			fromWeb.setTimeZone(utcZone);
			SimpleDateFormat myDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat myTimeFormat = new SimpleDateFormat("hh:mm a");
			for (ZVentsEvent event : zContent.events) {
				for (EventCalendar cal : result) 
					if (cal.getId().contains(event.vid)) {
						CalendarItem item = new CalendarItem();
						item.setName(event.name);
						item.setDescription(event.description);
						Date startTime = fromWeb.parse(event.startTime);
						Calendar calStartTime = Calendar.getInstance();
						calStartTime.setTimeZone(tZone);
						calStartTime.setTime(startTime);
						calStartTime.add(Calendar.MINUTE, startTime.getTimezoneOffset());
						item.setStartDate(myDateFormat.format(calStartTime.getTime()));
						item.setStartTime(myTimeFormat.format(calStartTime.getTime()));
						
						cal.addItem(item);
					}
			}
		}

		return result;
	}

	public static void main (String [] args) {
		ZVentsJson zvents = new ZVentsJson();
		try {
			List<EventCalendar> cals = zvents.getCalendarList(null,"Norman,OK",null,"CST",100,30,"nearby");
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
}
