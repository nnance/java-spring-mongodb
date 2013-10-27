package com.eventshero.api.model.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;
import com.eventshero.api.model.parsing.ESPNJson.TeamResult.TeamRow;

/*
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
*/

public class ESPNJson extends CalendarParser {

	public static class TeamResult {
		List<TeamRow> teams;
		
		public static class TeamRow {
			public String name;
			public String value;
			public String meta;
			public String tid;
			public String url;
			public String logoUrl;
		};
		
		public TeamResult() {
			teams = new ArrayList<TeamRow>();
		}
	}
	
	public class CalendarRow {
		String week;
		String date;
		String opponent;
		String time;
		String location;
		String description;
		String info;
		String season;
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

	protected String getSid(String url) {
		String sid = null;
	    String[] params = url.split("&");  
	    for(String param:params) {
	    	if (param.split("=")[0].equals("sid"))
	    		sid = param.split("=")[1];
	    }
		return sid;
	}
	
	protected List<TeamRow> getTeams(String sid, String url) throws IOException {
		String json = doGet(url);
		TeamResult teamResult = new Gson().fromJson(json, TeamResult.class);
		for(TeamRow team : teamResult.teams) {
			if (!team.name.contains("All")) {
				String teamId = team.value.split("_")[1];
				if (sid.equals("nfl"))
					team.logoUrl = "http://a.espncdn.com/i/teamlogos/nfl/sml/trans/" + teamId + ".gif";
			}
		}
		return teamResult.teams;
	}

	
	@Override
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws IOException {
		String sid = getSid(cat.getWebUrl());
		Set<EventCalendar> result = new HashSet<EventCalendar>();
		List<TeamRow> teamRows = getTeams(sid, cat.getWebUrl());
		for (TeamRow team : teamRows) {
			if (!team.name.contains("All")) {
//				String teamId = team.value.split("_")[1];
				EventCalendar cal = new EventCalendar();
				cal.setName(team.name);
				cal.setDescription(team.name + " Schedule 2013-2014");
				cal.setTimeZone("US/Eastern");
				cal.setLogoUrl(team.logoUrl);
				cal.setWebUrl("http://espn.go.com/travel/sports/calendar/getData.json?&sid="+sid+"&type=list&query=favs&myTeams=" + team.value);
				cal.setTeamId(team.value);
				cal.setPublicCalendar(true);
				cal.setWebSync(true);
				cal.setWebSyncParser(cat.getWebUrl());
				result.add(cal);
			}
		}
		return result;
	}

	@Override
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws Exception {
//		String json = doGet(cal.getWebUrl());
//		TeamResult teamResult = new Gson().fromJson(json, TeamResult.class);		
		return null;
	}
	
/*
 * This is old code attempted to use icalendar
 * this is use for calendars downloaded from http://espn.go.com/travel/sports/calendar/export/espnCal?&teams=7_201
 * 
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws IOException,
			ParseException, ParserException {
		String ical = doGet(cal.getWebUrl());
		ical = ical.replace("BYDAY=1S", "BYDAY=1SU");
		Set<CalendarItem> items = loadCalendar(ical,cal);
		return items;
	}

	private Set<CalendarItem> loadCalendar(String ical, EventCalendar cal) throws ParseException, IOException, ParserException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		StringReader sin = new StringReader(ical);
		CalendarBuilder builder = new CalendarBuilder();
			net.fortuna.ical4j.model.Calendar calendar = builder.build(sin);

			VTimeZone vtZone = (VTimeZone) calendar.getComponent("VTIMEZONE");
			String vtZoneId = vtZone.getTimeZoneId().getValue();
			java.util.TimeZone zone = java.util.TimeZone.getTimeZone(vtZoneId);

			SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
			myFormat.setTimeZone(zone);
			SimpleDateFormat myTime = new SimpleDateFormat("h:mm a");
			myTime.setTimeZone(zone);
			
			ComponentList list = calendar.getComponents("VEVENT");
			for(int i=0; i < list.size(); i++) {
				VEvent item = (VEvent) list.get(i);

				CalendarItem event = new CalendarItem();
				java.util.Calendar startDate = java.util.Calendar.getInstance();
				startDate.setTime(item.getStartDate().getDate());
				//required to adjust for EST. couldn't get it to work otherwise
				startDate.add(Calendar.HOUR, 1);
				String startDateStr = myFormat.format(startDate.getTime());
				event.setStartDate(startDateStr);
				String startTime = myTime.format(startDate.getTime());
				event.setStartTime(startTime);
				event.setDuration(180);
				event.setLocation(item.getLocation().getValue());
				event.setCalId("team="+cal.getTeamId()+"&date="+startDateStr);
				
				String[] teams = item.getSummary().getValue().split(" at ");
				if (teams[0].contains(cal.getName())) 
					event.setName("at " + teams[1]);
				else
					event.setName("vs " + teams[0]);

				result.add(event);				
			}
		return result;
	}
 */
	
}
