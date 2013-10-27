package com.eventshero.api.model.parsing;

import org.jsoup.nodes.Document;

import com.eventshero.api.model.EventCalendar;

public class ESPNMLBWeb extends ESPNWeb {

	protected int getSeasonStartMonth() {
		return 3;
	}

	protected int getSeasonStartYear() {
		return 2013;
	}
	
	protected int getTicketCol() {
		return 7;
	}
	
	protected boolean seperateTVCol() {
		return true;
	}
	
	protected String getLogoUrl(Document doc, EventCalendar cal) {
		return "baseball";
//		return "http://lh5.ggpht.com/-Gg9CeaSHKvXS0niJ8eRjM4FuVyRrUiWULf9RjHkY77Et2xiMQ1VxBS-dZrmntSBaz4AaSYqQGACJ3JudCGukQ";
	}

}
