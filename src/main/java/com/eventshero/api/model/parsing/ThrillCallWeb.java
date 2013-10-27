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

public class ThrillCallWeb extends CalendarParser {

	public class VenueRow {
		public String name;
		public String url;
		public String logo;
		public String location;
	}

	public class CalendarRow {
		String date;
		String artist;
		String time;
		String location;
		String description;
		String ticketUrl;
	}
	
	@Override
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws Exception {
		Set<EventCalendar> result = new HashSet<EventCalendar>();
		Document doc = Jsoup.connect(cat.getWebUrl()).timeout(10000).get();
		List<VenueRow> rows = getVenues(doc);
		for (VenueRow row : rows) {
			if (confirmEvents(row)) {
				EventCalendar cal = new EventCalendar();
				cal.setName(row.name);
				cal.setDescription(row.name + " Event Calendar");
				cal.setTimeZone("US/Eastern");
				cal.setWebUrl(row.url);
				cal.setLogoUrl(row.logo);
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
//		cal.setLogoUrl(getLogoUrl(calDoc,cal));
		cal.setLogoUrl("live-music");
		Set<CalendarItem> items = loadCalendar(calDoc,cal);
		return items;
	}


	protected List<VenueRow> getVenues(Document doc) throws IOException {
		List<VenueRow> result = new ArrayList<VenueRow>();
		Elements alphaLinks = doc.getElementsByClass("alphabeticLinks");
		if (alphaLinks.size() == 1) {
			Elements alphaRefs = alphaLinks.first().getElementsByTag("a");
			for (Element alphaRef : alphaRefs) {
				String url = alphaRef.attr("href");
				System.out.println("Calendar Catalog Loading Venues - " + url);
				Document venueDoc = Jsoup.connect("http://thrillcall.com"+url).timeout(10000).get();
				List<VenueRow> venues = getVenuesOnPage(venueDoc);
				result.addAll(venues);
			}
		}
		return result;
	}
	
	protected List<VenueRow> getVenuesOnPage(Document doc) throws IOException {
		List<VenueRow> result = new ArrayList<VenueRow>();
		Elements selects = doc.getElementsByClass("venueListing");
		for (Element select : selects) {
			VenueRow row = null;
			Elements options = select.getElementsByAttributeValue("itemprop","url");
			for (Element option : options) {
				if (option.hasAttr("itemprop")  && option.attr("itemprop").equals("url")) {
					String nameProp = getItemProp(option, "name");
					if (nameProp != null) {
						row = new VenueRow();
						row.name = nameProp;
						row.url = option.attr("href");
						row.logo = "http://lh3.ggpht.com/E0Xk-7Fkm-EdJSCx4HjRff1BWRFiU3nrfpCUR5hZ0l37-9bWvu5zgMWRrnRySxPmjEmpjKxms7lhFDI6AkDejA";

						String streetAddress = getItemProp(option.parent(), "streetAddress");
						String city = getItemProp(option.parent(), "addressLocality");
						String state = getItemProp(option.parent(), "addressRegion");
						String zip = getItemProp(option.parent(), "postalCode");
						if (streetAddress != null) {
							row.location = streetAddress + " " + city + ", " + state + " " + zip; 
						}
						else if (city != null) {
							row.location = city + ", " + state + " " + zip;
						}
						result.add(row);
					}
				}
			}
		}
		return result;
	}
	
	private String getItemProp(Element item, String propName) {
		String result = null;
		Elements prop = item.getElementsByAttributeValue("itemprop", propName);
		if (prop.size() == 1) {
			result = prop.first().html();
		}
		return result;
	}
	
	private boolean confirmEvents(VenueRow venue) throws IOException {
		boolean result = false;
		System.out.println("Calendar Catalog Confirming Events For - " + venue.url);
		Document doc = Jsoup.connect(venue.url).timeout(10000).get();
		result = doc.getElementsByClass("noContentAvailable").size() == 0;
		return result;
	}
	
	private Set<CalendarItem> loadCalendar(Document doc, EventCalendar cal) throws ParseException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		List<CalendarRow> rows = getCalendar(doc);
		for (CalendarRow row : rows) {
			if (!row.date.isEmpty()) {
				SimpleDateFormat fromWeb = new SimpleDateFormat("EEE, MMM dd, yyyy");
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

				Date scheduleDay = fromWeb.parse(row.date);
				Calendar jCal = Calendar.getInstance();
				jCal.setTime(scheduleDay);
				
				String reformattedStr = myFormat.format(jCal.getTime());

				CalendarItem event = new CalendarItem();
				event.setStartDate(reformattedStr);
				event.setName(row.artist);
				event.setAllDayEvent(true);
				event.setLocation(row.location);
				event.setDescription(row.description);
				event.setTicketUrl(row.ticketUrl);
				//build calendar id used for finding matches during syncing
				event.setCalId("artist=" + row.artist + "&date=" + row.date);
				result.add(event);				
			}
		}
		return result;
	}
	
	protected List<CalendarRow> getCalendar(Document doc) {		
		List<CalendarRow> result = new ArrayList<CalendarRow>();
		
		// get the featured show
		Elements featuredRows = doc.getElementsByClass("featuredShow");
		if (featuredRows.size() == 1) {
			Element featuredRow = featuredRows.first();
			CalendarRow row = new CalendarRow();
			row.artist = featuredRow.getElementsByClass("main_headliners").html();
			row.description = getItemProp(featuredRow,"name");
			row.location = getItemProp(featuredRow,"addressLocality") + " " + getItemProp(featuredRow,"addressRegion");
			row.date = featuredRow.getElementsByClass("calendarDate").first().text();
			row.ticketUrl = "http://thrillcall.com" + featuredRow.getElementsByTag("a").first().attr("href");
			result.add(row);
		}
		Elements eventRows = doc.getElementsByClass("eventTileContainer");
		for (Element eventRow : eventRows) {
			CalendarRow row = new CalendarRow();
			row.artist = eventRow.getElementsByClass("main_headliners").html();
			row.description = getItemProp(eventRow,"name");
			row.location = getItemProp(eventRow,"addressLocality") + " " + getItemProp(eventRow,"addressRegion");
			row.date = eventRow.getElementsByAttributeValue("itemprop", "startDate").first().attr("content");
			row.ticketUrl = "http://thrillcall.com" + eventRow.getElementsByTag("a").first().attr("href");
			if ((row.artist != null) && (row.artist.length() > 0))
				result.add(row);
		}
		return result;
	}
	
}
