package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class ESPNNASCAR extends ESPNWeb {

	protected List<TeamRow> getTeams(Document doc) throws IOException {
		List<TeamRow> result = new ArrayList<TeamRow>();
		Elements selects = doc.getElementsByClass("floatleft");
		
		TeamRow baseCal = new TeamRow();
		baseCal.teamName = "NASCAR Sprint Cup";
		baseCal.url = selects.first().getElementsByTag("a").first().attr("href");
		String[] urlSplit = baseCal.url.split("/");
		baseCal.url = baseCal.url.replace(urlSplit[urlSplit.length-1], "sprintcup");
		result.add(baseCal);
		
		for (Element select : selects) {
			TeamRow row = null;
			Elements options = select.getElementsByTag("a");
			for (Element option : options) {
				row = new TeamRow();
				row.teamName = option.text();
				row.url = option.attr("href");
				result.add(row);
			}
		}
		return result;
	}
	
	protected List<CalendarRow> getCalendar(Document doc) {		
		List<CalendarRow> result = new ArrayList<CalendarRow>();
		Elements tableRows = doc.getElementsByTag("table").first().getElementsByTag("tr");
		String seasonType = "reg";
		boolean hasWeekCol = false;
		for (Element row : tableRows) {
			if (!row.className().equals("stathead") && !row.className().equals("colhead")) {
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
						String value = col.text().trim();
						if (i == 0) 
							calRow.gameId = value;
						else if (i == 1) {
							value = value.substring(0,4) + " " + value.substring(5,8) + " " + value.substring(9);
							String[] dt = value.split(" ");
							calRow.date = dt[0] + " " + dt[1] + " " + dt[2];
							calRow.time = dt[3] + " " + dt[4];
						}
						else if (i == 2) {
							calRow.opponent = col.getElementsByTag("b").first().text();
							calRow.location = col.ownText();
						}
						else if (i == 3) {			
							Elements images = col.getElementsByTag("img");
							if (images.size() > 0) {
								String src = images.first().attr("src");
								if (src.contains("networkLogo_espn"))
									calRow.description = "TV: ESPN";
								else if (src.contains("networkLogo_abc"))
									calRow.description = "TV: ABC";
							}
							else calRow.description = "TV: " + value;
						}
						else if ((i == 4) && !resultsRow)
							calRow.info = "Tickets: " + col.html();
					}
					calRow.season = seasonType;
					result.add(calRow);
				}
			}
		}
		return result;
	}

//	protected String getLogoUrl(Document doc, EventCalendar cal) {
//		return null;
//	}
	protected int getSeasonStartMonth() {
		return 1;
	}
	
}
