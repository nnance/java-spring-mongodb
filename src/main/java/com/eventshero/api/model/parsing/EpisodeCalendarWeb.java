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

public class EpisodeCalendarWeb extends CalendarParser {

	public class ShowRow {
		public String name;
		public String description;
		public String url;
		public String logo;
		public String location;
	}

	public class CalendarRow {
		String episodeId;
		String date;
		String name;
		String time;
		String description;
	}
	
	@Override
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws Exception {
		Set<EventCalendar> result = new HashSet<EventCalendar>();
		Document doc = Jsoup.connect(cat.getWebUrl()).timeout(10000).get();
		String network = null;
		for (CalendarTag tag : tags)
			if (!tag.getName().equalsIgnoreCase("television"))
				network = tag.getName();
		List<ShowRow> rows = getShows(doc);
		for (ShowRow row : rows) {
			if (confirmEvents(row,network)) {
				EventCalendar cal = new EventCalendar();
				cal.setName(row.name);
				cal.setDescription(row.description);
				cal.setTimeZone("US/Eastern");
				cal.setIgnoreClientTimeZone(true);
				cal.setWebUrl(row.url);
//				cal.setLogoUrl("http://lh6.ggpht.com/Ca1PiiNOUqhjGh7z0gI28uunfg_3IY111NBDEy_15Xh7KxZ6tpwaGsr6lJV9PyjRMimPzk8c9t_psmU1JbVjXns");
				cal.setLogoUrl("tv");
				cal.setLocation(row.location);
				cal.setPublicCalendar(true);
				cal.setWebSync(true);
				cal.setWebSyncParser(cat.getWebSyncParser());
				result.add(cal);
			}
		}
		return result;
	}

	@Override
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws IOException,
			ParseException {
		Document calDoc = Jsoup.connect(cal.getWebUrl()).timeout(10000).get();
		Set<CalendarItem> items = loadCalendar(calDoc,cal);
		return items;
	}


	protected List<ShowRow> getShows(Document doc) throws IOException {
		List<ShowRow> results = new ArrayList<ShowRow>();
		Elements alphaLinks = doc.getElementsByAttributeValue("id", "show_pagination");
		if (alphaLinks.size() == 1) {
			Elements alphaRefs = alphaLinks.first().getElementsByTag("a");
			for (Element alphaRef : alphaRefs) {
				String url = alphaRef.attr("href");
				System.out.println("Calendar Catalog Loading Shows - " + url);
				Document showDoc = Jsoup.connect("http://www.episodecalendar.com"+url).timeout(10000).get();
				List<ShowRow> shows = getShowsOnPage(showDoc);
				results.addAll(shows);
			}
		}
		System.out.println("Possible eposides found: " + Integer.valueOf(results.size()));
		return results;
	}
	
	protected List<ShowRow> getShowsOnPage(Document doc) throws IOException {
		List<ShowRow> result = new ArrayList<ShowRow>();
		Element select = doc.getElementsByClass("show_list").get(1);
		ShowRow row = null;
		Elements options = select.getElementsByTag("li");
		for (Element option : options) {
			String status = option.getElementsByTag("span").first().html();
			String[] statusFlds = status.split(" ");
			if ((statusFlds.length > 1) && (Integer.valueOf(statusFlds[2]) > 150)) {
				Element show = option.getElementsByTag("a").first();
				String nameProp = show.html().trim();
				if (nameProp != null) {
					row = new ShowRow();
					row.name = nameProp;
					row.url = "http://www.episodecalendar.com"+ show.attr("href");
					result.add(row);
				}
			}
		}
		return result;
	}
	
	private boolean confirmEvents(ShowRow show, String network) throws IOException {
		System.out.println("Calendar Catalog Confirming Events For - " + show.url);
		Document doc = Jsoup.connect(show.url).timeout(10000).get();
		
		// find and set the logo
		Element banner = doc.getElementsByClass("show_banner").first();
		Element image = banner.getElementsByTag("img").first();
		show.logo = "http://www.episodecalendar.com"+image.attr("src").replace("original", "small");
		
		// find and set the network and description
		Element info = doc.getElementsByAttributeValue("id", "secondary_show_info").first();
		Elements infoLines = info.getElementsByTag("li");
		show.description = infoLines.get(0).text() + " " + infoLines.get(1).text();
		String timeAndNetwork = infoLines.get(4).text();
		show.location = timeAndNetwork.split(" ")[0];
		
		if (!show.location.equalsIgnoreCase(network.toLowerCase()))
			return false;
		Elements seasonList = doc.getElementsByClass("season_list");
		if ((seasonList != null) && (seasonList.size()>0)) {
			Elements episodes = doc.getElementsByClass("season_list").first().children();
			for (Element episode : episodes) {
				String date = episode.getElementsByClass("date").text();
				if (date.contains("2013") || date.contains("2014")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Set<CalendarItem> loadCalendar(Document doc, EventCalendar cal) throws ParseException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		List<CalendarRow> rows = getCalendar(doc);
		for (CalendarRow row : rows) {
			if (!row.date.isEmpty()) {
				SimpleDateFormat fromWeb = new SimpleDateFormat("MMMMM dd, yyyy");
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

				Date scheduleDay = fromWeb.parse(row.date);
				Calendar jCal = Calendar.getInstance();
				jCal.setTime(scheduleDay);
				
				String reformattedStr = myFormat.format(jCal.getTime());

				CalendarItem event = new CalendarItem();
				event.setStartDate(reformattedStr);
				event.setStartTime(row.time);
				event.setName(row.name);
				event.setDescription(row.description);
				//build calendar id used for finding matches during syncing
				event.setCalId(row.episodeId);
				result.add(event);				
			}
		}
		return result;
	}
	
	protected List<CalendarRow> getCalendar(Document doc) {		
		List<CalendarRow> result = new ArrayList<CalendarRow>();

		// find and set the network and description
		Element info = doc.getElementsByAttributeValue("id", "secondary_show_info").first();
		Elements infoLines = info.getElementsByTag("li");
		String[] timeAndNetwork = infoLines.get(4).text().split(" ");
		String time = "7";
		if (timeAndNetwork.length > 2) {
			time = timeAndNetwork[timeAndNetwork.length -1];
		}
		
		Elements episodes = doc.getElementsByClass("season_list").first().children();
		for (Element episode : episodes) {
			CalendarRow row = new CalendarRow();
			String name = episode.getElementsByClass("name").text();
			row.episodeId = name.split(":")[0];
			row.name = name.split(":")[1].trim();
			row.description = episode.getElementsByClass("overview").text();
			if (row.description.length() > 500) {
				row.description = row.description.substring(0, 495);
			}
			row.date = episode.getElementsByClass("date").text().replace("Air date", "").replace("st,", ",").replace("nd,",",").replace("th,", ",").replace("rd,",",").trim();
			if (time.length() < 3)
				row.time = time + ":00 PM";
			else if (time.length() < 6)
				row.time = time + " PM";
			else 
				row.time = time;
			result.add(row);
		}

		return result;
	}
	
}
