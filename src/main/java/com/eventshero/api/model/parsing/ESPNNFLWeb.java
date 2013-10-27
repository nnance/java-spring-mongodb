package com.eventshero.api.model.parsing;

import org.jsoup.nodes.Document;

import com.eventshero.api.model.EventCalendar;

public class ESPNNFLWeb extends ESPNWeb {

	protected int getSeasonStartMonth() {
		return 7;
	}

	protected int getSeasonStartYear() {
		return 2013;
	}
	
	protected String getLogoUrl(Document doc, EventCalendar cal) {
		return "football";
//		return "http://lh3.ggpht.com/n1UaLHgZSgxrMd33DtIK3FvAUllk5OFDlCVe2whFfIvUlHGrtOUrzqIh8eApYHjytwqHjS30EnRwOACuPmIa";
	}

}
