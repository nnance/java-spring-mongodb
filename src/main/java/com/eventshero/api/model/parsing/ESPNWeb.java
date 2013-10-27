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
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.eventshero.api.model.CalendarCatalog;
import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.CalendarTag;
import com.eventshero.api.model.EventCalendar;

public class ESPNWeb extends CalendarParser {

	public class TeamRow {
		public String teamName;
		public String url;
		public String logo;
	}

	public class CalendarRow {
		String gameId;
		String date;
		String opponent;
		String time;
		String location;
		String description;
		String info;
		String season;
		String results;
		String recapUrl;
		String ticketUrl;
	}
	
	@Override
	public Set<EventCalendar> getCalendarList(CalendarCatalog cat, CalendarTag[] tags) throws Exception {
		Set<EventCalendar> result = new HashSet<EventCalendar>();
		Document doc = Jsoup.connect(cat.getWebUrl()).timeout(10000).get();
		List<TeamRow> teamRows = getTeams(doc);
		for (TeamRow team : teamRows) {
			EventCalendar cal = new EventCalendar();
			cal.setName(team.teamName);
			cal.setDescription(team.teamName + " Schedule 2013-2014");
			cal.setTimeZone("US/Eastern");
			cal.setWebUrl(team.url);
			cal.setPublicCalendar(true);
			cal.setWebSync(true);
			cal.setWebSyncParser(cat.getWebSyncParser());
			result.add(cal);
		}
		return result;
	}

	@Override
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws IOException,
			ParseException {
		Document calDoc = Jsoup.connect(cal.getWebUrl()).timeout(10000).get();
		cal.setLogoUrl(getLogoUrl(calDoc,cal));
		Set<CalendarItem> items = loadCalendar(calDoc,cal);
		return items;
	}

	protected List<TeamRow> getTeams(Document doc) throws IOException {
		List<TeamRow> result = new ArrayList<TeamRow>();
		Elements selects = doc.getElementsByClass("medium-logos");
		for (Element select : selects) {
			TeamRow row = null;
			Elements options = select.getElementsByTag("a");
			for (Element option : options) {
				if (option.className().equalsIgnoreCase("bi")) {
					row = new TeamRow();
					row.teamName = option.html();
				}
				if (option.html().equalsIgnoreCase("schedule")) {
					row.url = "http://espn.go.com" + option.attr("href");
					result.add(row);
				}
			}
		}
		return result;
	}
	
	protected Set<CalendarItem> loadCalendar(Document doc, EventCalendar cal) throws ParseException {
		Set<CalendarItem> result = new HashSet<CalendarItem>();
		List<CalendarRow> rows = getCalendar(doc);
		for (CalendarRow row : rows) {
			if (!row.date.isEmpty()) {
				SimpleDateFormat fromWeb = new SimpleDateFormat("EEE, MMM dd");
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

				// make sure the date will parse
				String[] dates = row.date.split(" ");
				if ((dates.length == 3) && (dates[1].length() > 3))
					row.date = dates[0] + " " + dates[1].substring(0, 3) + " " + dates [2];
				Date scheduleDay = fromWeb.parse(row.date);
				Calendar jCal = Calendar.getInstance();
				jCal.setTime(scheduleDay);
				// default year is not set properly because it isn't coming from the web site 
				// so we are hard coding the default for now
				if (jCal.get(Calendar.MONTH) < getSeasonStartMonth()) 
					jCal.set(Calendar.YEAR, getSeasonStartYear()+1);
				else
					jCal.set(Calendar.YEAR, getSeasonStartYear());
				
				String reformattedStr = myFormat.format(jCal.getTime());

				CalendarItem event = new CalendarItem();
				event.setStartDate(reformattedStr);
				event.setName(row.opponent.replaceFirst("@", "at"));
				event.setStartTime(row.time);
				event.setLocation(row.location);
				if (row.results != null) {
					String teamName = cal.getName();
					if (teamName.contains("("))
						teamName = teamName.substring(0, teamName.indexOf("(")).trim();
					event.setTeamName(teamName);

					String opponentName = event.getName().substring(3);
					if (opponentName.contains("#"))
						opponentName = opponentName.substring(opponentName.indexOf(" ")).trim();
					event.setOpponentName(opponentName);

					String score = row.results.split(" ")[1];
					String[] scores = score.split("-");
					event.setTeamScore(scores[0]);
					event.setOpponentScore(scores[1]);
					if (row.recapUrl.contains("recap"))
						event.setRecapUrl("http://scores.espn.go.com" + row.recapUrl.replace("recap","gamecast"));
					String value = row.results;
					if (row.season.equals("pre"))
						value = "Preseason results: " + value;
					else 
						value = "Results: " + value;
					value = value.replaceFirst(" W ", " Win ");
					value = value.replaceFirst(" L ", " Loss ");
					event.setDescription(value);
				}
				else {
					event.setDescription(row.description);
					event.setTicketUrl(row.ticketUrl);
				}
				event.setInfo(row.info);
				//build calendar id used for finding matches during syncing
				event.setCalId("season=" + row.season + "&gameid=" + row.gameId);
				result.add(event);				
			}
		}
		return result;
	}
	
	protected int getSeasonStartMonth() {
		return 7;
	}
	
	protected int getSeasonStartYear() {
		return 2013;
	}
	
	protected int getTicketCol() {
		return 4;
	}
	
	protected boolean seperateTVCol() {
		return false;
	}
	
	protected String getLogoUrl(Document doc, EventCalendar cal) {
		return "sports";
//		String[] urlSplit = cal.getWebUrl().split("/");
//		String[] paramSplit = cal.getWebUrl().split("=");
//		String teamId = null;
//		if (paramSplit.length > 1)
//			teamId = paramSplit[1];
//		else
//			teamId = urlSplit[8];
//		return "http://a.espncdn.com/combiner/i?img=i/teamlogos/"+ urlSplit[3] +"/500/" + teamId + ".png&h=80&w=80&scale=crop&transparent=true";
	}
			
	protected List<CalendarRow> getCalendar(Document doc) {		
		List<CalendarRow> result = new ArrayList<CalendarRow>();
		Elements tableRows = doc.getElementsByTag("table").first().getElementsByTag("tr");
		String seasonType = "reg";
		boolean hasWeekCol = false;
		for (Element row : tableRows) {
			if (row.className().equals("stathead")) {
				if (row.text().contains("Regular Season"))
					seasonType = "reg";
				else if (row.text().contains("Preseason"))
					seasonType = "pre";
				else if (row.text().contains("Postseason"))
					seasonType = "post";
			}
			else if (row.className().equals("colhead")) {
				String colText = row.getElementsByTag("td").first().text();
				hasWeekCol = colText.equalsIgnoreCase("wk") || colText.equalsIgnoreCase("rnd");
			}
			else  {
				boolean resultsRow = false;
				Elements cols = row.getElementsByTag("td");
				if (!hasWeekCol) {
					Tag tag = Tag.valueOf("td");
					Element element = new Element(tag,doc.baseUri());
					element.text(Integer.toString(result.size()+1));
					cols.add(0, element);
				}
				if ((cols.size() > 2) && (!cols.get(3).text().trim().equalsIgnoreCase("Canceled"))) {
					CalendarRow calRow = new CalendarRow();
					for (int i = 0; i < cols.size(); i++) {
						Element col = cols.get(i);
						String value = col.text().trim().replace("&nbsp;", " ");
						if (i == 0) 
							calRow.gameId = value;
						else if (i == 1)
							calRow.date = value;
						else if (i == 2) {
							calRow.opponent = value;
						}
						else if (i == 3) {							
							resultsRow = col.getElementsByClass("game-status").size() > 0;
							if (resultsRow) {
								calRow.results = value;
								calRow.recapUrl = col.getElementsByTag("a").first().attr("href");
								calRow.time = "1:00 PM";
							}
							else if (value.equalsIgnoreCase("tbd") || value.equalsIgnoreCase("tba")) {
								calRow.time = "1:00 PM";
							}
							else {
								if (value.contains(" ")) {
									String[] strs = value.split(" ");
									calRow.time = strs[0] + " " + strs[1];
									if (!seperateTVCol()) {
										Elements images = col.getElementsByTag("img");
										if (images.size() > 0) {
											String src = images.first().attr("src");
											if (src.contains("networkLogo_espn"))
												calRow.description = "TV: ESPN";
											else if (src.contains("networkLogo_abc"))
												calRow.description = "TV: ABC";
										}
										else if ((strs.length == 3) && (!strs[2].equalsIgnoreCase("ET")))
											calRow.description = "TV: " + strs[2];
									}
								}
								else {
									calRow.time = "1:00 PM";
								}
							}
						}
						else if ((i == 4) && !resultsRow && seperateTVCol()) {
							Elements images = col.getElementsByTag("img");
							if (images.size() > 0) {
								String src = images.first().attr("src");
								if (src.contains("networkLogo_espn"))
									calRow.description = "TV: ESPN";
								else if (src.contains("networkLogo_abc"))
									calRow.description = "TV: ABC";
							}
							else if (!col.html().equalsIgnoreCase("&nbsp;")) 
								calRow.description = "TV: " + col.text();
							
						}
						else if (i == getTicketCol() && !resultsRow) {
							Elements ticketLinks = col.getElementsByTag("a");
							if (ticketLinks.size() > 0)
								for (Element link : ticketLinks) {
									calRow.ticketUrl = link.attr("href");
								}
						}
					}
					calRow.season = seasonType;
					result.add(calRow);
				}
			}
		}
		return result;
	}
	
}
