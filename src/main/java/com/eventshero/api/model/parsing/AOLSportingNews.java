package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;

public class AOLSportingNews extends CalendarParser {

	public class TeamRow {
		public String teamName;
		public String url;
	}
	
	public class CalendarRow {
		public List<String> columns = new ArrayList<String>();
	}
	
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws IOException, ParseException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		if (cal.getItems().size() == 0) {
			Document calDoc = Jsoup.connect(cal.getWebUrl()).timeout(10000).get();
			cal.setLogoUrl(getLogoUrl(calDoc));
			result = loadCalendar(calDoc);
		}
		return result;
	}
	
	private Set<CalendarItem> loadCalendar(Document doc) throws ParseException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		List<CalendarRow> rows = getCalendar(doc);
		boolean startLoading = false;
		int colOffset = 0;
		for (CalendarRow row : rows) {
			if (!startLoading) {
				if (row.columns.size() > 3)  {
					for (int i = 0; i < row.columns.size(); i++) {
						String col = row.columns.get(i);
						if (col.equalsIgnoreCase("TIME (ET)")) {
							colOffset = i - 2;
							startLoading = true;
							break;
						}
					}
				}
			}
			else {
				if (!row.columns.get(colOffset).equals("")) {
					SimpleDateFormat fromWeb = new SimpleDateFormat("EEE, MMM dd");
					SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
	
					// default year is not set properly because it isn't coming from the web site 
					// so we are hard coding the default for now
					String webDate = row.columns.get(colOffset);
					Date scheduleDay = fromWeb.parse(webDate);
					Calendar jCal = Calendar.getInstance();
					jCal.setTime(scheduleDay);
					if (jCal.get(Calendar.MONTH) < 7) 
						jCal.set(Calendar.YEAR, 2013);
					else
						jCal.set(Calendar.YEAR, 2012);
					
					String reformattedStr = myFormat.format(jCal.getTime());

					CalendarItem event = new CalendarItem();
					event.setStartDate(reformattedStr);
					event.setName(row.columns.get(colOffset + 1));
					event.setStartTime(row.columns.get(colOffset + 2));
					event.setLocation(row.columns.get(colOffset + 3));
					event.setInfo(row.columns.get(colOffset + 4));
					result.add(event);				
				}
			}
		}
		return result;
	}
	
	public String getLogoUrl(Document doc) {
		String result = null;
		Elements logos = doc.getElementsByClass("logo");
		for (Element logo : logos) {
			Elements images = logo.getElementsByTag("img");
			for (Element image : images) {
				result = image.attr("src");
			}
		}
		return result;
	}
	
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws IOException, ParseException {
		Set<EventCalendar> result = new HashSet<EventCalendar>();
		Document doc = Jsoup.connect(cat.getWebUrl()).timeout(10000).get();
		List<TeamRow> teamRows = getTeams(doc);
		for (TeamRow team : teamRows) {
			EventCalendar cal = new EventCalendar();
			cal.setName(team.teamName);
			cal.setDescription(team.teamName + " Schedule 2012-2013");
			cal.setTimeZone("US/Eastern");
			cal.setWebUrl(team.url);
			cal.setPublicCalendar(true);
			cal.setWebSync(true);
			cal.setWebSyncParser("AOL SportingNews");
			result.add(cal);
		}
		return result;
	}
	
	public List<TeamRow> getTeams(String url) throws IOException {
		Document doc = Jsoup.connect(url).timeout(10000).get();
		return getTeams(doc);
	}
	
	public List<TeamRow> getTeams(Document doc) throws IOException {
		List<TeamRow> result = new ArrayList<TeamRow>();
		Elements selects = doc.getElementsByTag("select");
		for (Element select : selects) {
			if (select.attr("name").equalsIgnoreCase("team_type")) {
				Elements options = select.getElementsByTag("option");
				for (Element option : options) {
					String href = option.attr("value");
					if (href.contains("aol.sportingnews.com")) {
						TeamRow row = new TeamRow();
						row.teamName = option.text();
						row.url = option.attr("value");
						result.add(row);
					}
				}
			}
		}
		return result;
	}
	
	public List<CalendarRow> getCalendar(Document doc) {
		List<CalendarRow> result = new ArrayList<CalendarRow>();
		Elements cols = doc.getElementsByTag("th");
//		Elements captions = doc.getElementsByTag("caption");
//		for (Element caption : captions) {
		for (Element col : cols) {
			if (col.html().equalsIgnoreCase("TIME (ET)")) {
//			if (caption.html().equalsIgnoreCase("Regular Season")) {
//				Elements rows = caption.parent().getElementsByTag("tr");
				Elements rows = col.parent().parent().getElementsByTag("tr");
				for (Element row : rows) {
					Elements columns = row.getElementsByTag("td");
					if (columns.size() == 0) {
						columns = row.getElementsByTag("th");
					}
					CalendarRow calRow = new CalendarRow();
					if (columns.size() > 0) {
						result.add(calRow);
					}
					for (Element column : columns) {
						String value = column.html();
						if (value.contains("aol.sportingnews.com")) {
							Elements children = column.children();
							if (children.size() > 0) {
								for (Element child : children) {
									value = value.replace(child.outerHtml(), "").concat(" " + child.text() + " ");
								}
							}
						}
						calRow.columns.add(value.trim());
					}
				}
			}
		}
		return result;
	}
}
