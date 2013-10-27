package com.eventshero.api.model.parsing;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;

import com.eventshero.api.model.EventCalendar;

public class ESPNNCAAWeb extends ESPNWeb {

	protected List<TeamRow> getTeams(Document doc) throws IOException {
		List<TeamRow> results = super.getTeams(doc);
		for (TeamRow row : results) {
			if (row.url.contains("ncf"))
				row.teamName = row.teamName + " (FB)";
			else if (row.url.contains("ncb"))
				row.teamName = row.teamName + " (BB)";
			else if (row.url.contains("ncw"))
				row.teamName = row.teamName + " (WBB)";
		}
		return results;
	}
	
	protected String getLogoUrl(Document doc, EventCalendar cal) {
		String result = "";
		if (cal.getName().contains("(FB)"))
			result = "football";
//			result = "http://lh3.ggpht.com/n1UaLHgZSgxrMd33DtIK3FvAUllk5OFDlCVe2whFfIvUlHGrtOUrzqIh8eApYHjytwqHjS30EnRwOACuPmIa";
		else if (cal.getName().contains("(BB)") || cal.getName().contains("(WBB)"))
			result = "basketball";
//			result = "http://lh3.ggpht.com/BwlN5MaxVaOnnoEZJIDq8UX1j-nCk45QwQcWf5cx2PwO3nazNB47VygDvzBraeh36MDjI0HXQTR8GSXeM29xsJg";
		else 
			result = "sports";
//			result = "http://a.espncdn.com/combiner/i?img=i/teamlogos/ncaa/500/" + cal.getWebUrl().split("=")[1] + ".png&h=80&w=80&scale=crop&transparent=true";
		return result;
	}
}
