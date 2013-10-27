package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eventshero.api.model.CalendarItem;
import com.eventshero.api.model.EventCalendar;

public class ESPNNBAWeb extends ESPNWeb {

	@Override
	public Set<CalendarItem> loadCalendar(EventCalendar cal) throws IOException,
			ParseException {
		Document calDoc = Jsoup.connect(cal.getWebUrl()).timeout(10000).get();
		cal.setLogoUrl(getLogoUrl(calDoc,cal));
		// find the current season page
		Element seasonNav = calDoc.getElementsByClass("mod-thirdnav-tabs").first();
		Elements seasons = seasonNav.getElementsByTag("li");
		String activeSeasonUrl = seasons.get(seasons.size()-1).getElementsByTag("a").first().attr("href");
		calDoc = Jsoup.connect(activeSeasonUrl).timeout(10000).get();
		
		Set<CalendarItem> items = loadCalendar(calDoc,cal);
		return items;
	}

	protected int getSeasonStartMonth() {
		return 9;
	}
	
	protected int getTicketCol() {
		return 5;
	}
	
	protected boolean seperateTVCol() {
		return true;
	}
	
	protected String getLogoUrl(Document doc, EventCalendar cal) {
		return "basketball";
//		return "http://lh3.ggpht.com/BwlN5MaxVaOnnoEZJIDq8UX1j-nCk45QwQcWf5cx2PwO3nazNB47VygDvzBraeh36MDjI0HXQTR8GSXeM29xsJg";
	}
}
